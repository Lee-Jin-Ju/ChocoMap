package com.choco.chocomap.searchmap.Dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.choco.chocomap.searchmap.Dto.KeywordDto;

@Mapper
public interface SearchMapDao {
    
	public void lockKeyword(String keyword);
    public void insertKeyword(String keyword);
    public List<KeywordDto> selectKeywordList();
    
}
