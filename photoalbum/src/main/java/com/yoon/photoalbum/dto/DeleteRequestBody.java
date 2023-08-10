package com.yoon.photoalbum.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteRequestBody {

    private Long[] photoIds;
}
