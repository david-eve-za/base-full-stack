package gon.cue.basefullstack;

import com.github.javafaker.Faker;
import gon.cue.basefullstack.dao.ICategoryDao;
import gon.cue.basefullstack.model.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class BaseFullStackApplication implements ApplicationRunner {

    private final ICategoryDao categoryDao;

    public BaseFullStackApplication(ICategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public static void main(String[] args) {
        SpringApplication.run(BaseFullStackApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Application started");
        if (categoryDao.count() == 0) {
            log.info("No categories found, creating default categories");
            Faker faker = new Faker();
            for (int i = 0; i < 10; i++) {
                Category category = new Category();
                category.setName(faker.commerce().department());
                category.setDescription(faker.lorem().sentence());
                categoryDao.save(category);
                log.info("Category created: {}", category);
            }
        }
    }
}
