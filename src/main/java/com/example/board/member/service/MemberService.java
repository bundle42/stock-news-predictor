package com.example.board.member.service;

import com.example.board.member.dto.MemberDTO;
import com.example.board.member.entity.MemberEntity;
import com.example.board.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void save(MemberDTO memberDTO) {
        // 0. dto의 password 암호화
        String encodedPassword = passwordEncoder.encode(memberDTO.getMemberPassword());
        memberDTO.setMemberPassword(encodedPassword);
        // 1. dto -> entity 변환
        // 2. repository의 save 메서드 호출
        MemberEntity memberEntity = MemberEntity.toMemberEntity(memberDTO);
        memberRepository.save(memberEntity);
        // repository의 save메서드 호출 (조건. entity객체를 넘겨줘야 함)
    }

    public List<MemberDTO> findAll() {
        List<MemberEntity> memberEntityList = memberRepository.findAll();
        List<MemberDTO> memberDTOList = new ArrayList<>();
        for (MemberEntity memberEntity: memberEntityList) {
            memberDTOList.add(MemberDTO.toMemberDTO(memberEntity));
        }
        return memberDTOList;
    }

    public MemberDTO findByEmail(String email) {
        Optional<MemberEntity> optionalMemberEntity = memberRepository.findByMemberEmail(email);

        if(optionalMemberEntity.isPresent()) {
            return MemberDTO.toMemberDTO(optionalMemberEntity.get());
        } else {
            return null;
        }
    }

    @Transactional
    public void update(MemberDTO memberDTO) {

        MemberEntity memberEntity = memberRepository
                .findById(memberDTO.getId())
                .orElseThrow();

        memberEntity.setMemberName(memberDTO.getMemberName());

        // 비밀번호를 입력 했다면
        if(memberDTO.getMemberPassword() != null &&
                !memberDTO.getMemberPassword().isBlank()) {

            // 새롭게 갱신
            String encodedPassword = passwordEncoder.encode(memberDTO.getMemberPassword());
            memberEntity.setMemberPassword(encodedPassword);
        }
    }

    @Transactional
    public void changeRole(Long id, String role) {
        MemberEntity member = memberRepository.findById(id)
                .orElseThrow();

        member.setMemberRole(role);
    }

    public void deleteById(Long id) {
        memberRepository.deleteById(id);
    }

    public void deleteByEmail(String email) {
        MemberEntity member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        memberRepository.delete(member);
    }

    public boolean emailExists(String memberEmail) {
        return memberRepository.findByMemberEmail(memberEmail).isPresent();
    }

    public MemberEntity login(String email, String password) {

        MemberEntity member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new RuntimeException("EMAIL_NOT_FOUND"));

        if (!passwordEncoder.matches(password, member.getMemberPassword())) {
            throw new RuntimeException("INVALID_PASSWORD");
        }

        return member;
    }
}












