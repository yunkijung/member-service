package com.loglrs.memberservice.api.member.controller;



import com.loglrs.memberservice.api.member.dto.login.MemberLoginDto;
import com.loglrs.memberservice.api.member.dto.login.MemberLoginResponseDto;
import com.loglrs.memberservice.api.member.dto.member_info.MemberInfoDto;
import com.loglrs.memberservice.api.member.dto.signup.MemberSignupDto;
import com.loglrs.memberservice.api.member.dto.signup.MemberSignupResponseDto;
import com.loglrs.memberservice.domain.component_service.member.service.MemberComponentService;
import com.loglrs.memberservice.domain.member.entity.Member;
import com.loglrs.memberservice.domain.member.service.MemberService;
import com.loglrs.memberservice.domain.member.service.MemberServiceImpl;
import com.loglrs.memberservice.domain.refreshtoken.entity.RefreshToken;
import com.loglrs.memberservice.domain.refreshtoken.service.RefreshTokenService;
import com.loglrs.memberservice.domain.role.entity.Role;
import com.loglrs.memberservice.security.jwt.util.IfLogin;
import com.loglrs.memberservice.security.jwt.util.JwtTokenizer;
import com.loglrs.memberservice.security.jwt.util.LoginUserDto;
import com.loglrs.memberservice.vo.Greeting;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/members")
public class MemberController {

    private final Environment env;
    private final Greeting greeting;

    private final JwtTokenizer jwtTokenizer;
    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    private final MemberComponentService memberComponentService;

    @GetMapping("/welcome")
    public ResponseEntity welcome() {
        return new ResponseEntity(greeting.getMessage(), HttpStatus.OK);
//        return new ResponseEntity(env.getProperty("greeting.message"), HttpStatus.OK);
    }

    @PostMapping("/greeting")
    public ResponseEntity greeting() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//        Member member = mapper.map(memberDto, Member.class);

        return ResponseEntity.status(HttpStatus.OK).body(greeting.getMessage());
    }

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody @Valid MemberSignupDto memberSignupDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        Member member = new Member(memberSignupDto.getEmail()
                , passwordEncoder.encode(memberSignupDto.getPassword())
                , memberSignupDto.getName()
                , Boolean.FALSE);


        Member savedMember = memberComponentService.signUp(member);

        MemberSignupResponseDto memberSignupResponse = new MemberSignupResponseDto();
        memberSignupResponse.setMemberId(savedMember.getId());
        memberSignupResponse.setName(savedMember.getName());
        memberSignupResponse.setEmail(savedMember.getEmail());

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("member", memberSignupResponse);
        // 회원가입
        return new ResponseEntity(resultMap, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid MemberLoginDto loginDto, BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        // email이 없을 경우 Exception이 발생한다. Global Exception에 대한 처리가 필요하다.
        Member member = memberService.findByEmail(loginDto.getEmail());
        if(!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        // List<Role> ===> List<String>
        List<String> roles = member.getRoles().stream().map(Role::getName).collect(Collectors.toList());

        // JWT토큰을 생성하였다. jwt라이브러리를 이용하여 생성.
        String accessToken = jwtTokenizer.createAccessToken(member.getId(), member.getEmail(), member.getName(), roles);
        String refreshToken = jwtTokenizer.createRefreshToken(member.getId(), member.getEmail(), member.getName(), roles);

        // RefreshToken을 DB에 저장한다. 성능 때문에 DB가 아니라 Redis에 저장하는 것이 좋다.
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setValue(refreshToken);
        refreshTokenEntity.setMemberId(member.getId());
        refreshTokenService.addRefreshToken(refreshTokenEntity);

        // create a cookie
        Cookie cookie = new Cookie("refreshToken", refreshToken);

        // expires in 7 days
        cookie.setMaxAge(7 * 24 * 60 * 60);

        // optional properties
//        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        // add cookie to response
        response.addCookie(cookie);

        MemberLoginResponseDto loginResponse = MemberLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken("httoOnly")
                .memberId(member.getId())
                .nickname(member.getName())
                .build();

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("member", loginResponse);

        return new ResponseEntity(resultMap, HttpStatus.OK);
    }

    @DeleteMapping("/logout")
    public ResponseEntity logout(@CookieValue(name = "refreshToken", required = true) String token, HttpServletResponse response) {
        refreshTokenService.deleteRefreshToken(token);
        // create a cookie
        Cookie cookie = new Cookie("refreshToken", "");

        // expires in 7 days
        cookie.setMaxAge(0);

        // optional properties
//        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        // add cookie to response
        response.addCookie(cookie);

        return new ResponseEntity(HttpStatus.OK);
    }

    /*
    1. 전달받은 유저의 아이디로 유저가 존재하는지 확인한다.
    2. RefreshToken이 유효한지 체크한다.
    3. AccessToken을 발급하여 기존 RefreshToken과 함께 응답한다.
     */
    @PostMapping("/refreshToken")
    public ResponseEntity requestRefresh(@CookieValue(name = "refreshToken", required = true) String token) {
        RefreshToken refreshToken = refreshTokenService.findRefreshToken(token).orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));
        Claims claims = jwtTokenizer.parseRefreshToken(refreshToken.getValue());

        Long memberId = Long.valueOf((Integer)claims.get("memberId"));

        Member member = memberService.findById(memberId);


        List roles = (List) claims.get("roles");
        String email = claims.getSubject();

        String accessToken = jwtTokenizer.createAccessToken(memberId, email, member.getName(), roles);

        MemberLoginResponseDto loginResponse = MemberLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken("httpOnly")
                .memberId(member.getId())
                .nickname(member.getName())
                .build();

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("member", loginResponse);

        return new ResponseEntity(resultMap, HttpStatus.OK);
    }

    @GetMapping("/info")
    public ResponseEntity userinfo(@IfLogin LoginUserDto loginUserDto) {
        Member member = memberService.findById(loginUserDto.getMemberId());
        MemberInfoDto memberInfoDto = new MemberInfoDto(member);

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("member", memberInfoDto);

        return new ResponseEntity(resultMap, HttpStatus.OK);
    }


    @GetMapping("/check/email")
    public ResponseEntity checkEmail(@RequestParam String email) {
        Optional<Member> findMember = memberService.checkByEmail(email);
        Boolean result;
        if(findMember.isEmpty()) {
            result = false;
        } else {
            result = true;
        }
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("exist", result);

        return new ResponseEntity(resultMap, HttpStatus.OK);
    }

    @GetMapping("/check/name")
    public ResponseEntity checkName(@RequestParam String name) {
        Optional<Member> findMember = memberService.checkByName(name);
        Boolean result;
        if(findMember.isEmpty()) {
            result = false;
        } else {
            result = true;
        }
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("exist", result);

        return new ResponseEntity(resultMap, HttpStatus.OK);
    }


}
