package com.ef.wss.newscollect.service;



import java.util.List;

import com.ef.wss.newscollect.pojo.LinkType;


public interface LinkTypeService {


    void insertLinkTypes(List<LinkType> linkTypes);

    LinkType queryLinkTypeByUrl(String url);

}
