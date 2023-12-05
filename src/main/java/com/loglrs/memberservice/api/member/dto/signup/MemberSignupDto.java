package com.loglrs.memberservice.api.member.dto.signup;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignupDto {

    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
            message = "이메일 형식을 맞춰야합니다")
    private String email;

    @NotEmpty
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{7,16}$",
            message = "비밀번호는 영문+숫자+특수문자를 포함한 8~20자여야 합니다")
    private String password;

    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z가-힣\\\\s]{2,15}",
            message = "이름은 영문자, 한글, 공백포함 2글자부터 15글자까지 가능합니다.")
    private String name;

    @NotNull
    private LocalDate birth;

    @NotNull
    private Long genderId;

    @NotNull
    private Long religionId;

    @NotNull
    private Long occupationId;

    @NotNull
    private Long raceId;

    @NotNull
    private Long nationalityId;

//
//    @NotEmpty
//    @Pattern(regexp = "^[MF]{1}$", message = "성별은 'M' 또는 'F'로 입력해야 합니다")
//    private String gender;
}