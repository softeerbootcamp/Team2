package softeer.carbook.domain.hashtag.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import softeer.carbook.domain.hashtag.dto.HashtagSearchResponse;
import softeer.carbook.domain.hashtag.model.Hashtag;
import softeer.carbook.domain.hashtag.repository.HashtagRepository;
import softeer.carbook.domain.post.service.PostService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HashtagServiceTest {
    @InjectMocks
    private HashtagService hashtagService;

    @Mock
    private HashtagRepository hashtagRepository;

    @ParameterizedTest
    @ValueSource(strings = {"맑", "맑음"})
    @DisplayName("해시태그 검색 기능 테스트")
    void searchHashTag(String keyword) {
        // given
        given(hashtagRepository.searchHashtagByPrefix(keyword)).willReturn(new ArrayList<>(List.of(
                new Hashtag(1, "맑음")
        )));

        // when
        HashtagSearchResponse response = hashtagService.searchHashTag(keyword);

        // then
        List<Hashtag> result = response.getHashtags();
        Hashtag hashtag = result.get(0);
        assertThat(result.size()).isEqualTo(1);
        assertThat(hashtag.getId()).isEqualTo(1);
        assertThat(hashtag.getTag()).isEqualTo("맑음");
        verify(hashtagRepository).searchHashtagByPrefix(keyword);
    }
}
