import javax.swing.*;//подключаем библиотеку swing, она нужна для того чтобы
//показывать пользователю графический интерфейс (окно игры)
import java.awt.*;//эта библиотека также нужна для отображения интерфейсы, в данном случае картинок (бонусы для змейки, саму змейку)
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;//подключение библиотеки для генерирования случайных значений


public class GameField extends JPanel implements ActionListener {//создаем класс GameField, в котором будем прописывать всю игровую логику для змейки, также мы
    //наследуем его от JPanel для того, чтобы отображать бонусы и саму змейку
    private final int SIZE = 320;
    private final int DOT_SIZE = 16;//размер в пикселях сколько будет занимать бонус для змейки и одна секция для змейки
    private final int ALL_DOTS = 400;//максимальное кол-во единиц (dots), которое может поместиться на игровом поле
    private Image dot;//отображение секции змейки
    private Image apple;//отображение бонуса змейки
    private int appleX;//позиция бонуса для змейки по оси X
    private int appleY;//позиция бонуса для змейки по оси Y
    private int[] x = new int[ALL_DOTS];//все положения змейки по оси x
    private int[] y = new int[ALL_DOTS];//все положения змейки по оси y
    private int dots;//размер змейки в данный момент времени
    private Timer timer;//это таймер - он будет нужен для расчета скорости обновления экрана, то есть кол-ва фпс в игре
    //или скорости змейки
    private boolean left = false;//направление змейки влево
    private boolean right = false;//направление змейки вправо
    private boolean up = true;//направление змейки вверх
    private boolean down = false;//направление змейки вниз
    private boolean inGame = true;//проверка на то играем ли мы сейчас или уже проиграли


    public GameField(){
        setBackground(Color.black);//тут мы используем метод setBackground, он нам нужен для того, чтобы
        //назначить цвет нашего фона, в данном случае мы назначили черный
        loadImages();//загрузка изображений
        initGame();//инициализация игры
        addKeyListener(new FieldKeyListener());
        setFocusable(true);
    }

    public void initGame(){//инициализация начала игры
        dots = 3;//задание размера змейки 3 точки
        for (int i = 0; i < dots; i++){//задание положения змейки на поле
            x[i] = 48 - i * DOT_SIZE;
            y[i] = 48;
        }

        timer = new Timer(250,this);//создание таймера
        //для обозначения скорости передвижения змейки
        timer.start();//запуск таймера
        createApple();//вызов метода для создания бонуса для змейки
    }

    public void createApple(){//метод для создания бонуса для змейки
        appleX = new Random().nextInt(20) * DOT_SIZE;//задание рандомной позиции бонуса по оси X
        appleY = new Random().nextInt(20) * DOT_SIZE;//задание рандомной позиции бонуса по оси Y
    }

    public void loadImages(){
        ImageIcon imageIconApple = new ImageIcon("apple.png");//тут мы создаем переменную с типом ImageIcon
        //и ложим в эту переменную картинку бонуса змейки
        apple = imageIconApple.getImage();//присваиваем полю apple картинку с бонусом
        ImageIcon imageIconDot = new ImageIcon("dot.png");//тут мы создаем переменную с типом ImageIcon
        //и ложим в эту переменную картинку секции змейки
        dot = imageIconDot.getImage();//присваиваем полю dot картинку с секцией змейки
    }

    @Override
    protected void paintComponent(Graphics graphics){//метод для перерисовки картинки игры
        super.paintComponent(graphics);

        if (inGame == true) {
            graphics.drawImage(apple, appleX, appleY, this);//рисовка бонуса

            for (int i = 0; i < dots; i++) {//рисовка змейки
                graphics.drawImage(dot, x[i], y[i], this);
            }
        }
        else{
            String str = "Game Over";
            graphics.setColor(Color.white);
            graphics.drawString(str,125,SIZE/2);
        }
    }

    public void move(){//метод для определения движения змейки
        for (int i = dots; i > 0; i--) {//тут мы передвигаем змейку по оси X и по оси Y
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        //тут мы передвигаем голову змейки, в зависимости от того куда она поворачивает
        if(left == true){
            x[0] -= DOT_SIZE;
        }
        if(right == true){
            x[0] += DOT_SIZE;
        }
        if(up == true){
            y[0] -= DOT_SIZE;
        }
        if(down == true){
            y[0] += DOT_SIZE;
        }
    }

    public void checkApple(){//метод для проверки не собрали ли мы бонус
        if(x[0] == appleX && y[0] == appleY){
            dots++;
            createApple();
        }
    }

    public void checkCollisions(){//метод для проверки столкнулись ли мы сами с собой или со стенкой
        for (int i = dots; i > 0; i--) {
            if(i > 4 && x[0] == x[i] && y[0] == y[i]){
                inGame = false;
            }
        }

        if(x[0] > SIZE){
            inGame = false;
        }
        if(x[0] < 0){
            inGame = false;
        }
        if(y[0] > SIZE){
            inGame = false;
        }
        if(y[0] < 0){
            inGame = false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {//этот метод вызывается каждые 250 милисекунд, то есть каждый раз когда таймер идет на новый круг
        //этот метод предназначен для того, чтобы создавать новый бонус, проверять колизию, а также двигать змейку и рисовать заново изображение
        if(inGame == true){
            checkApple();
            checkCollisions();
            move();
        }

        repaint();
    }

    class FieldKeyListener extends KeyAdapter{//класс для определения нажатия кнопок
        @Override
        public void keyPressed(KeyEvent event){
            super.keyPressed(event);//здесь мы считываем нажатие кнопки на клавиатуре
            int key = event.getKeyCode();
            //тут мы в зависимости от той кнопки, которую нажали задаем движение змейки
            if(key == KeyEvent.VK_LEFT && !right){
                left = true;
                up = false;
                down = false;
            }
            if(key == KeyEvent.VK_RIGHT && !left){
                right = true;
                up = false;
                down = false;
            }
            if(key == KeyEvent.VK_UP && !down){
                right = false;
                up = true;
                left = false;
            }
            if(key == KeyEvent.VK_DOWN && !up){
                right = false;
                down = true;
                left = false;
            }
        }
    }
}
