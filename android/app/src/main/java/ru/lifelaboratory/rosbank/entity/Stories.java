package ru.lifelaboratory.rosbank.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Stories {

    @SerializedName("id_stories")
    private Integer id;
    private List<String> image;

}
