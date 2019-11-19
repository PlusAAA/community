package life.majiang.community.controller;

import life.majiang.community.dto.AccessTokenDTO;
import life.majiang.community.dto.GithubUser;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.swing.plaf.synth.SynthTextAreaUI;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id")
    private String client_id;
    @Value("${github.client.secret")
    private String client_secret;
    @Value("${github.redirect.uri}")
    private String redirect_uri;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletRequest request) {
        AccessTokenDTO tokenDTO = new AccessTokenDTO(client_id, client_secret, code,
                redirect_uri,state);
        String accessToken = githubProvider.getAccessToken(tokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);
        githubUser = new GithubUser("小白白", Integer.toUnsignedLong(1030422807), "我是一只小菜皮卡丘");
        System.out.println(githubUser.getName());
        if(githubUser != null){
            // 登录成功
            User user = new User(githubUser.getName(), String.valueOf(githubUser.getId()), UUID.randomUUID().toString(),
                    System.currentTimeMillis(), System.currentTimeMillis());
            userMapper.insert(user);
            request.getSession().setAttribute("user", githubUser);
            return "redirect:/";
        } else {
            // 登录失败
            return "redirect:/";
        }
    }
}
