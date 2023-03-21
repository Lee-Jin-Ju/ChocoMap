package com.choco.chocomap.searchmap.common;

import lombok.Getter;

@Getter
public enum ApiFieldForm {
	KAKAO("documents", "place_name", "road_address_name", "address_name", "place_url", "category_name", "phone", "Kakao"),
    NAVER("items", "title", "roadAddress", "address", "link", "category", "telephone", "Naver");

    private String listKey;
    private String placeKey;
    private String roadAddressKey;
    private String addressKey;
    private String urlKey;
    private String categoryKey;
    private String phoneKey;
    private String siteKey;

    ApiFieldForm(String listKey, String placeKey, String roadAddressKey, String addressKey, String urlKey, String categoryKey, String phoneKey, String siteKey) {
        this.listKey = listKey;
        this.placeKey = placeKey;
        this.roadAddressKey = roadAddressKey;
        this.addressKey = addressKey;
        this.urlKey = urlKey;
        this.categoryKey = categoryKey;
        this.phoneKey = phoneKey;
        this.siteKey = siteKey;
    }
}
