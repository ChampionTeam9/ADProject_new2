package com.ad.teamnine.model;

public class RecipeDTO {
    private Integer id;
    private String name;
    private String description;

    // 默认构造函数
    public RecipeDTO() {}

    // 带参数的构造函数
    public RecipeDTO(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Getter 和 Setter 方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}