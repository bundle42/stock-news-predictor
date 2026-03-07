package com.example.board.member.service;

import com.example.board.member.entity.MemberEntity;
import com.example.board.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        MemberEntity member = memberRepository
                .findByMemberEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

        return new User(
                member.getMemberEmail(),
                member.getMemberPassword(),
                Collections.emptyList()
        );
    }
}