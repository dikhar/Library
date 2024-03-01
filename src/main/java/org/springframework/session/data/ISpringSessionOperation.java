package org.springframework.session.data;


import org.framework.SpringSessionData;

public interface ISpringSessionOperation {
    void saveAsSecondary(SpringSessionData springSessionData);
}
