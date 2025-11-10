package com.nailpos.nailposapi.service;

import com.nailpos.nailposapi.dto.ServiceItemRequestDTO;
import com.nailpos.nailposapi.model.ServiceItem; // <-- Đổi
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IServiceItemService {

    Page<ServiceItem> getServiceItems(String search, String category, Pageable pageable);

    ServiceItem getServiceItemById(Long id);

    ServiceItem createServiceItem(ServiceItemRequestDTO serviceDTO);

    ServiceItem updateServiceItem(Long id, ServiceItemRequestDTO serviceDTO);

    void deleteServiceItem(Long id);
}