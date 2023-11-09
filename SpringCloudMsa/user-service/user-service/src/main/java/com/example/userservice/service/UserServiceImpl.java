package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    //Environment env;

    //Email(여기서는 Id)를 이용해서 계정 체크하는 메소드
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username은 Email로 규정
        UserEntity userEntity = userRepository.findByEmail(username);

        // Email로 찾아도 없으면, 예외 반환
        if (userEntity == null) {
            throw new UsernameNotFoundException(username);
        }

        // DB에서 Email로 조회 -> DB의 pwd와 입력된 pwd 비교 -> 인증 -> 검색된 사용자 값 반환 (여기서 할 일)
        // Security 패키지의 User 클래스에 반환할 값을 넣어 줌
        return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(),
                true, true, true, true,
                new ArrayList<>()); // ArrayList에는 권한을 넣어주면 된다. 지금은 없어서 빈 ArrayList

    }


    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        //UserDto -> UserEntity 변환 작업
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd())); // **

        userRepository.save(userEntity);

        return mapper.map(userEntity, UserDto.class);

        //UserDto returnUserDto = mapper.map(userEntity, UserDto.class);

        //return returnUserDto;
    }

    //유저 ID 로 조회
    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        //조회할 유저가 없을 경우
        if (userEntity == null)
            throw new UsernameNotFoundException("User not found");

        // UserEntity -> UserDto로 변환
        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

        List<ResponseOrder> orders = new ArrayList<>();
        userDto.setOrders(orders);

        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

//        if(userEntity == null){
//            throw new UsernameNotFoundException(username);
//        }

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

        return userDto;
    }
}


