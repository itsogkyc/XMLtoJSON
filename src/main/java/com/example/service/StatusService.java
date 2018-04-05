package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.Status;
import com.example.repository.StatusRepository;

@Service
@Transactional
public class StatusService {

	@Autowired
	private StatusRepository statusRepository;

	public void save(Status status) {
		statusRepository.save(status);
	}

	public List<Status> findAll() {
		return statusRepository.findAll();
	}

	public Long countRecord() {
		return statusRepository.count();
	}

	public void updateStatusValue(int success_x2j, int fail_x2j, int successj2x, int fail_j2x) {
		statusRepository.updateStatusValue(success_x2j, fail_x2j, successj2x, fail_j2x);
	}

}
