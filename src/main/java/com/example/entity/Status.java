package com.example.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Status {

	/**
	 * xml <-> json 전환값을 저장하는 Status Entity
	 */
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long seq;

	private int success_x2j;
	private int fail_x2j;
	private int success_j2x;
	private int fail_j2x;

	public Status() {
	}

	public Status(int success_x2j, int fail_x2j, int success_j2x, int fail_j2x) {
		this.success_x2j = success_x2j;
		this.fail_x2j = fail_x2j;
		this.success_j2x = success_j2x;
		this.fail_j2x = fail_j2x;
	}

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}
	
	public int getSuccess_x2j() {
		return success_x2j;
	}

	public void setSuccess_x2j(int success_x2j) {
		this.success_x2j = success_x2j;
	}

	public int getFail_x2j() {
		return fail_x2j;
	}

	public void setFail_x2j(int fail_x2j) {
		this.fail_x2j = fail_x2j;
	}

	public int getSuccess_j2x() {
		return success_j2x;
	}

	public void setSuccess_j2x(int success_j2x) {
		this.success_j2x = success_j2x;
	}

	public int getFail_j2x() {
		return fail_j2x;
	}

	public void setFail_j2x(int fail_j2x) {
		this.fail_j2x = fail_j2x;
	}

}
