package com.mickey305.sample;

import com.mickey305.common.v2.json.model.Token;
import com.mickey305.common.v2.json.model.Type;

public class ChildToken extends Token {
    private String freeSpace;

    public ChildToken(Type type, String str) {
        super(type, str);
    }

    public String getFreeSpace() {
        return freeSpace;
    }

    public void setFreeSpace(String freeSpace) {
        this.freeSpace = freeSpace;
    }
}
