package com.choco.chocomap.searchmap.common;

import lombok.Getter;

@Getter
public enum ApiFlagForm {
	INIT("init"),
    LIST_PLUS("listPlus"),
    RENAME("reName"),
    REMAP_NAME("reMapName");
	
    private String flagKey;

    ApiFlagForm(String flagKey) {
        this.flagKey = flagKey;
    }
}
