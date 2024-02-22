package com.customer.customer.entities.categories;

//import com.customer.customer.Auditing.Auditable;

import com.customer.customer.Auditing.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CategoryMetaDataField extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotEmpty(message = "metaname can't empty")
    private String metaName;
//    @ManyToMany(mappedBy ="categoryMetaDataFields")
//    private Set<SubCategory> subCategories;
    @OneToMany(mappedBy = "categoryMetaDataField")
    Set<CategoryMetaDataFieldValues>categoryMetaDataFieldValuesSet;


}
