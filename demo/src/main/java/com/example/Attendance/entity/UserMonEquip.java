package com.example.Attendance.entity;

import javax.persistence.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.*;


@Entity
@Table(name = "usermonequip")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMonEquip {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usermonequip_seq")
	@SequenceGenerator(name = "usermonequip_seq", sequenceName = "USERMONEQUIP_seq", allocationSize = 1)
	private Long id;
	
	@JoinColumn(name ="usermon_id")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
	private UserMon usermon_id;
	
	@OneToOne
	@JoinColumn(name = "itemmaster_id")
	private ItemMaster itemmaster_id;
	
	private Integer slot_number;
	

}
