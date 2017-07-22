
@org.hibernate.annotations.GenericGenerator(
        name = "PERFECT_GENERATOR",
        strategy = "enhanced-sequence",
        parameters = {
                @org.hibernate.annotations.Parameter(name = "sequence_name", value = "bangbang_sequence"),
                @org.hibernate.annotations.Parameter(name = "initial_value", value = "1")
        }
)
package com.stephen.bangbang.domain;

interface Constants {
    String PERFECT_SEQUENCE = "PERFECT_GENERATOR";
}