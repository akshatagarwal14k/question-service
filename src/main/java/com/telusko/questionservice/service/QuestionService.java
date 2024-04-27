package com.telusko.questionservice.service;

import com.telusko.questionservice.dao.QuestionDao;
import com.telusko.questionservice.model.Question;
import com.telusko.questionservice.model.QuestionWrapper;
import com.telusko.questionservice.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    QuestionDao questionDao;
    public ResponseEntity<List<Question>> getAllQuestions() {
        try{
            return new ResponseEntity<>(questionDao.findAll(), HttpStatus.OK);
        } catch(Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<List<Question>> getQuestionsByCategory(String category) {
        try{
            return new ResponseEntity<>(questionDao.findByCategory(category), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Question> addQuestion(Question question){
        try{
            return new ResponseEntity<>(questionDao.save(question), HttpStatus.CREATED);
        }catch(Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new Question(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> deleteQuestion(Integer id){
        try{
            questionDao.deleteById(id);
            return new ResponseEntity<String>("deleted", HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<String>("failure", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Question> saveOrUpdateQuestion(Question question) {
        try {
            Optional<Question> existingQuestion = questionDao.findById(question.getId());
            if (existingQuestion.isEmpty()) {
                return new ResponseEntity<>(questionDao.save(question), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(questionDao.save(question), HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new Question(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<List<Integer>> getQuestionsForQuiz
            (String categoryName, int numQuestions) {
        List<Integer> questions = questionDao.findRandomQuestionsByCategory(categoryName,numQuestions);
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(List<Integer> questionIds) {
        List<QuestionWrapper> wrappers = new ArrayList<>();
        List<Question> questions = new ArrayList<>();
        for(Integer id : questionIds){
            questions.add(questionDao.findById(id).get());
        }

        for(Question question: questions){
            QuestionWrapper qr = new QuestionWrapper();
            qr.setId(question.getId());
            qr.setQuestionTitle(question.getQuestionTitle());
            qr.setOption1(question.getOption1());
            qr.setOption2(question.getOption2());
            qr.setOption3(question.getOption3());
            qr.setOption4(question.getOption4());
            wrappers.add(qr);
        }
        return new ResponseEntity<>(wrappers, HttpStatus.OK);
    }

    public ResponseEntity<Integer> getScore(List<Response> responses) {

        int right = 0;
        for(Response response : responses){
            Question question = questionDao.findById(response.getId()).get();
            if(response.getResponse().equals(question.getRightAnswer()))
                right++;
        }
        return new ResponseEntity<>(right, HttpStatus.OK);
    }
}
