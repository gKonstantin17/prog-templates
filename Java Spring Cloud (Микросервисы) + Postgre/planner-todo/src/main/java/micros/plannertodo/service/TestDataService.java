package micros.plannertodo.service;

import jakarta.transaction.Transactional;
import micros.plannerentity.entity.Category;
import micros.plannerentity.entity.Priority;
import micros.plannerentity.entity.Task;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
@Transactional
public class TestDataService {
    private final TaskService tService;
    private final PriorityService pService;
    private final CategoryService cService;

    public TestDataService(TaskService tService,
                              PriorityService pService,
                              CategoryService cService)
    {
        this.tService = tService ;
        this.pService = pService ;
        this.cService = cService ;
    }

    @KafkaListener(topics = "mytopic")
    public void listenKafka(Long userId) {
        // выполнение логики при получении данных
        System.out.println("new userId =" + userId);
        //init(userId);
    }

    public void init(String userId) {
        Priority prior1 = new Priority();
        prior1.setColor("#fff");
        prior1.setTitle("Важный");
        prior1.setUserId(userId);

        Priority prior2 = new Priority();
        prior2.setColor("#ffe");
        prior2.setTitle("Не важный");
        prior2.setUserId(userId);

        pService.add(prior1);
        pService.add(prior2);


        Category cat1 = new Category();
        cat1.setTitle("Работа");
        cat1.setUserId(userId);

        Category cat2 = new Category();
        cat2.setTitle("Семья");
        cat2.setUserId(userId);

        cService.add(cat1);
        cService.add(cat2);

        // tomorrow
        Date tommorow = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(tommorow);
        c.add(Calendar.DATE,1);
        tommorow = c.getTime();

        // week
        Date week = new Date();
        Calendar c2 = Calendar.getInstance();
        c2.setTime(week);
        c2.add(Calendar.DATE,7);
        week = c2.getTime();


        Task task1 = new Task();
        task1.setTitle("Покушать");
        task1.setCategory(cat1);
        task1.setPriority(prior1);
        task1.setCompleted(true);
        task1.setTaskDate(tommorow);
        task1.setUserId(userId);

        Task task2 = new Task();
        task2.setTitle("Поспать");
        task2.setCategory(cat2);
        task2.setPriority(prior2);
        task2.setCompleted(false);
        task2.setTaskDate(week);
        task2.setUserId(userId);

        tService.add(task1);
        tService.add(task2);
    }
}
