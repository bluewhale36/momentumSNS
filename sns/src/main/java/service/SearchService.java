package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import dao.IF_MainDAO;
import dao.IF_SearchDao;
import vo.PostVO;
import vo.SearchVO;

@Service
public class SearchService implements IF_SearchService {
	@Inject
	IF_SearchDao sDao;
	@Inject
	IF_MainDAO mDao;
	
	//keyword를 저장하기 위한 메서드
	@Override
	public void insertKeyWord(String keyWord) throws Exception {
		sDao.insertKeyWord(keyWord);
	}

	//keyWord를 가져오기 위한 메서드
	@Override
	public List<String> selectKeyWord() throws Exception {
		List<String> keyWord = new ArrayList<>();
		List<SearchVO> searchList = sDao.selectKeyWord();
		for(SearchVO s : searchList) {
			keyWord.add(s.getKeyWord());
		}
		return keyWord;
	}

	//검색 결과를 가져오기 위한 메서드
	@Override
	public List<PostVO> selectSearchList(Map map) throws Exception {
		List<PostVO> pVO = sDao.selectSearchList(map);
		for(PostVO p : pVO) {
			List<String> file = mDao.postAttach(p.getNo());
			p.setFileName(file.toArray(new String[file.size()]));
		}
		return pVO;
	}

	@Override
	public List<PostVO> searchRepost(List<Integer> no) throws Exception {
		List<PostVO> pVO = sDao.searchRepost(no);
		for(PostVO p : pVO) {
			List<String> file = mDao.postAttach(p.getNo());
			p.setFileName(file.toArray(new String[file.size()]));
		}
		return pVO;
	}
}
