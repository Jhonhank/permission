package com.kevin.param;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.List;

@Getter
@Setter
public class TestVo {

    @NotBlank
    private String msg;

    @NotNull(message = "id不能为空")
    @Max(value = 10,message = "id不能大于10")
    @Min(value=0,message = "id至少大于等于0")
    private Integer id;

    private List<String> str;
}
