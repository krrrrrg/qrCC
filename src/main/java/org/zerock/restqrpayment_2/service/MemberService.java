package org.zerock.restqrpayment_2.service;

import org.zerock.restqrpayment_2.dto.MemberDTO;

import java.util.List;

public interface MemberService {
    String register(MemberDTO memberDTO); // Create
    MemberDTO read(String mid);           // Read
    void modify(MemberDTO memberDTO);     // Update
    void remove(String mid);              // Delete

    List<MemberDTO> getMemberList();
    
}
