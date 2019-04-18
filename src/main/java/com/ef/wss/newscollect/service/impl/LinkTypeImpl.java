package com.ef.wss.newscollect.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ef.wss.newscollect.mapper.LinkTypeMapper;
import com.ef.wss.newscollect.pojo.LinkType;
import com.ef.wss.newscollect.service.LinkTypeService;

@Component
public class LinkTypeImpl implements LinkTypeService {
	@Autowired
	private LinkTypeMapper mapper;
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public void insertLinkType(LinkType linkType) {
		try {
			mapper.insert(linkType);
		} catch (Exception e) {
			logger.error("插入链接出错！");
		}
	}

	@Override
	public void insertLinkTypes(List<LinkType> linkTypes) {

		for (LinkType linkType : linkTypes) {
			insertLinkType(linkType);
		}
	}

	@Override
	public LinkType queryLinkTypeByUrl(String url) {
		LinkType linkType = null;
		try {
			linkType = mapper.seleLinkTypeByUrl(url);
		} catch (Exception e) {
			logger.error("查询链接种类出错！{}", e.getMessage());
			return null;
		}
		return linkType;
	}

}
