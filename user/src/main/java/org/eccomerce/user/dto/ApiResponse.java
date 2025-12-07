package org.eccomerce.user.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApiResponse {
    LocalDateTime timestamp = LocalDateTime.now();
    String message;
    Object data;
    Object error;
    boolean success;
    public  ApiResponse(String message,Object data,Object error,boolean status)
    {
        this.message=message;
        this.data=data;
        this.error=error;
        this.success=status;
    }



}
