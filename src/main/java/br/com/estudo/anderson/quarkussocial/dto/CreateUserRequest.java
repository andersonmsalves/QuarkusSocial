package br.com.estudo.anderson.quarkussocial.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//@Getter
//@Setter
@Data
public class CreateUserRequest {

    @NotBlank(message = "Name is Required")
    private String name;

    @NotNull(message = "Age is Required")
    private Integer age;

    /*public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }*/
}
