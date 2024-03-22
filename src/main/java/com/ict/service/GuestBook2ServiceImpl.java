package com.ict.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ict.dao.DAO;
import com.ict.dao.VO;

@Service // DAO 호출
public class GuestBook2ServiceImpl implements GuestBook2Service{
	private static final Logger logger = LoggerFactory.getLogger(GuestBook2ServiceImpl.class);
	
	@Autowired // DAO 를 사용하기 위해 Autowired 한다.
	private DAO dao ;

	@Override
	public List<VO> getGuestBook2List() {
		return  dao.getGuestBook2List();
	}

	@Override
	public VO getGuestBook2Detail(String idx) {
		
		return dao.getGuestBook2Detail(idx);
	}

	@Override
	public int getGuestBook2Insert(VO vo) {
		
		return dao.getGuestBook2Insert(vo);
	}

	@Override
	public int getGuestBook2Delete(String idx) {

		return dao.getGuestBook2Delete(idx);
	}
	

	@Override
	public int getGuestBook2Update(VO vo) {

		return dao.getGuestBook2Update(vo);
	}
	
}
