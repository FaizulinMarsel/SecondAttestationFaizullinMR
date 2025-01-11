package resolvers;

import com.fasterxml.jackson.databind.ObjectMapper;
import helpers.PlayerGenerator;
import helpers.Players;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.helpers.AnnotationHelper;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.service.PlayerService;
import ru.inno.course.player.service.PlayerServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;


public class PlayerServiceImplResolvers implements ParameterResolver, AfterEachCallback {

    private static final Path DATA_FILE_PATH = Path.of("./data.json");

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        Files.deleteIfExists(DATA_FILE_PATH);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(PlayerService.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Players annotation = AnnotationHelper.findAnnotation(parameterContext.getAnnotatedElement(), Players.class);
        int num = annotation.value();
        Set<Player> players;
        if (num > 0) {
            players = PlayerGenerator.generate(num);
            createFile(players);
        }
        return new PlayerServiceImpl();
    }

    private void createFile(Set<Player> players) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(DATA_FILE_PATH.toFile(), players);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
