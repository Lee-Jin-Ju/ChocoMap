package com.choco.chocomap.searchmap.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class PlaceDto {
	
	private String place_name;
	private String place_road_address_name;
	private String place_address_name;
	private String place_category_name;
	private String place_phone;
	private String place_url;
	private String place_search_site;
	
	PlaceDto (String place_name, String place_road_address_name, String place_address_name, String place_category_name, String place_phone, String place_url, String place_search_site){
		this.place_name = place_name;
		this.place_road_address_name = place_road_address_name;
		this.place_address_name = place_address_name;
		this.place_category_name = place_category_name;
		this.place_phone = place_phone;
		this.place_url = place_url;
		this.place_search_site = place_search_site;
	}
}
