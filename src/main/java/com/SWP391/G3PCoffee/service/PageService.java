package com.SWP391.G3PCoffee.service;

import com.SWP391.G3PCoffee.model.RequestParamForPage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PageService {


    public Pageable getPage(RequestParamForPage requestParamForPage) {
        return this.getPage(requestParamForPage.getPage(),
                requestParamForPage.getSize(),
                requestParamForPage.getSortDir(),
                requestParamForPage.getSortField());
    }

    public Pageable getPage(int page, int size, String sortDir, String sortField) {
        Pageable pageable;
        if ("asc".equals(sortDir)) {
            pageable = PageRequest.of(page - 1, size, Sort.Direction.ASC, sortField);
        } else {
            pageable = PageRequest.of(page - 1, size, Sort.Direction.DESC, sortField);
        }
        return pageable;
    }

    public Pageable getPageWithUnSort(int page, int size) {
        return PageRequest.of(page - 1, size);
    }
}