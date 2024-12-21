package com.sontung.blood.model;

import androidx.annotation.DrawableRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class OnBoardingItem {
    private String title;
    private String description;
    @DrawableRes
    private int imageResId;
}