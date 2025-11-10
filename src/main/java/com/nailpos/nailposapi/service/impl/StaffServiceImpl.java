package com.nailpos.nailposapi.service.impl;

import com.nailpos.nailposapi.model.Staff;
import com.nailpos.nailposapi.repository.StaffRepository;
import com.nailpos.nailposapi.service.IStaffService;
import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements IStaffService {

    private final StaffRepository staffRepository;

    @Override
    @Cacheable("staffList")
    public List<Staff> getAllStaff(){
        return staffRepository.findAll();
    }
}
