package com.choco.chocomap.searchmap.Dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class SearchListPlaceDto {
	
	private List<PlaceDto> listPlace;
	private int place_tot_count;
	
	SearchListPlaceDto (List<PlaceDto> listPlace, int place_tot_count){
		this.listPlace = listPlace;
		this.place_tot_count = place_tot_count;
	}
}
