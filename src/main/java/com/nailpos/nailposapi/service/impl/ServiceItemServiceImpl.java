package com.nailpos.nailposapi.service.impl;

import com.nailpos.nailposapi.dto.ServiceItemRequestDTO;
import com.nailpos.nailposapi.exception.ResourceNotFoundException;
import com.nailpos.nailposapi.mapper.ServiceItemMapper;
import com.nailpos.nailposapi.model.ServiceItem;
import com.nailpos.nailposapi.repository.ServiceItemRepository;
import com.nailpos.nailposapi.service.IServiceItemService;
import com.nailpos.nailposapi.specification.ServiceItemSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service; // <-- Annotation @Service của Spring
import org.springframework.transaction.annotation.Transactional;

/**
 * Đây là @Service của Spring, xử lý logic cho ServiceItem
 */
@Service
@RequiredArgsConstructor
public class ServiceItemServiceImpl implements IServiceItemService {

    // Tiêm (Inject) đúng Repository và Mapper đã đổi tên
    private final ServiceItemRepository serviceItemRepository;
    private final ServiceItemMapper serviceItemMapper;

    @Override
    @Cacheable("serviceItems") // Đổi tên cache
    @Transactional(readOnly = true)
    public Page<ServiceItem> getServiceItems(String search, String category, Pageable pageable) {
        Specification<ServiceItem> spec = ServiceItemSpecification.filterBy(search, category);
        return serviceItemRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceItem getServiceItemById(Long id) {
        return serviceItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceItem", "id", id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "serviceItems", allEntries = true) // Đổi tên cache
    public ServiceItem createServiceItem(ServiceItemRequestDTO serviceDTO) {
        ServiceItem newServiceItem = serviceItemMapper.toEntity(serviceDTO);
        return serviceItemRepository.save(newServiceItem);
    }

    @Override
    @Transactional
    @CacheEvict(value = "serviceItems", allEntries = true) // Đổi tên cache
    public ServiceItem updateServiceItem(Long id, ServiceItemRequestDTO serviceDTO) {
        ServiceItem serviceItem = getServiceItemById(id); // Đã có check 404
        serviceItemMapper.updateFromDTO(serviceDTO, serviceItem);
        return serviceItemRepository.save(serviceItem);
    }

    @Override
    @Transactional
    @CacheEvict(value = "serviceItems", allEntries = true) // Đổi tên cache
    public void deleteServiceItem(Long id) {
        if (!serviceItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("ServiceItem", "id", id);
        }
        serviceItemRepository.deleteById(id); // Kích hoạt @SQLDelete
    }
}