package com.r17dame.connecttool.callback;
import com.r17dame.connecttool.datamodel.AuthorizeInfo;

import java.util.UUID;

public interface AuthorizeCallback {
    void authCallback(AuthorizeInfo value );
}
