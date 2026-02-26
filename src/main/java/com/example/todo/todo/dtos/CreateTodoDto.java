package com.example.todo.todo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateTodoDto (
    @NotBlank(message = "Title must not be blank")
    @Size(min = 2, message = "Title must be longer than 1 character") 
    String title,

   @NotNull(message = "Completed must be true or false")
    Boolean completed,

    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be positive")
    Long categoryId  
        )
{ 
}
