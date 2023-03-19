package com.choco.chocomap.searchmap.infrastructure.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.choco.chocomap.searchmap.Dto.PlaceDto;

@Mapper
public interface SearchKeywordMapper {
	public void insertSearchKeyword(PlaceDto params);
}


