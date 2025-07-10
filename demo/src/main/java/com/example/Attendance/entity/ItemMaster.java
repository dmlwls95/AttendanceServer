package com.example.Attendance.entity;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "itemmaster")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemMaster {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itemmaster_seq")
	@SequenceGenerator(name = "itemmaster_seq",sequenceName = "ITEMMASTER_SEQ", allocationSize = 1)
	private Long id;
	
	private String name;
	private ItemType type;
	private String effect_key;
	private String resource_key;
	private Integer max_Stack;

}
