package in.koala.serviceImpl;

import in.koala.annotation.Auth;
import in.koala.domain.ChatMessage;
import in.koala.domain.Criteria;
import in.koala.enums.ChatType;
import in.koala.enums.TokenType;
import in.koala.mapper.ChatMessageMapper;
import in.koala.mapper.UserMapper;
import in.koala.service.ChatService;
import in.koala.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {
    @Resource
    private SimpMessagingTemplate template;

    @Resource(name="redisTemplate")
    private SetOperations<String, String> setOps;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ChatMessageMapper chatMessageMapper;

    @Value("${chat.room.id}")
    private String roomId;

    @Override
    public void send(Message<ChatMessage> message){
        String token = StompHeaderAccessor.wrap(message).getFirstNativeHeader("Authorization");
        Long id = Long.valueOf(String.valueOf(jwtUtil.getClaimsFromJwt(token, TokenType.ACCESS).get("id")));

        ChatMessage chatMessage = message.getPayload();
        chatMessage.setSender(id);
        chatMessageMapper.insertMessage(chatMessage);
        template.convertAndSend("/sub/" + roomId, chatMessage);
    }

    @Override
    public void imageSend(){}

    @Override
    public String getMemberCount(){
        ChatMessage message = ChatMessage.builder()
                                .sender(Long.valueOf(0))
                                .message(setOps.size("member").toString())
                                .type(ChatType.ACCESS)
                                .build();
        template.convertAndSend("/sub/" + roomId, message);
        return message.getMessage();
    }

    @Override
    public String getMemberName(Long id){
        return userMapper.getNormalUserById(id).getNickname();
    }

    @Auth
    @Override
    public List<ChatMessage> getMessageList(Criteria criteria){
        return chatMessageMapper.getMessageList(criteria);
    }

}