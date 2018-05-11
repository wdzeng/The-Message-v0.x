package the_message_server;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class Server {

    public Server() {

        for (int i = 0; i < 11; i++)
            rooms.add(new Room(i));

        try {
            ss = new ServerSocket(10222);
            while (true) {
                try {
                    User p = new User(ss.accept());
                    boolean isFixing = false;
                    if (isFixing) {
                        p.print("�����n�J:���A�����פ�");
                        continue;
                    }
                    else
                        p.print("�i�J�n�J�e��");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (BindException e1) {
            System.out.println("���A���w�ҰʡA�εo�ͳs����e�ΡC");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @SuppressWarnings("serial")
    class NoCardException extends Exception {

        public NoCardException(String ms) {
            super(ms);
        }

    }

    class Player {

        public Player(User user) {
            this.user = user;
        }
        public int cardId = 0; // �L���b�ϥΪ��d�P;
        public String chr = null;
        public boolean hasPrepareOldSkill = false;
        public ArrayList<Integer> hc = new ArrayList<>(); // ��P�N��
        public int idy; // 1��� 2�x�� 3��o 0�L����
        public int isAlive = 1; // 1��ܦs�� 2���` 3����
        public boolean isChrCov = false;
        public boolean isIdyCov = true;
        public boolean isReady = false;
        public ArrayList<Integer> itl = new ArrayList<>(); // �����N��
        public int ltd = 0; // 0�L 1�Q��w 2�Q�ժ� 3�I�򪬺A
        public Room room;
        public int seat = -1;
        public int sit = 0; // "��Hetc" //1��H 2�ժ� 3�ձ� 4�N�H 5�N�d 6�ѯ}
        public ArrayList<Integer> skills = new ArrayList<>();
        public int status = 1; // 1�b�u 2�U�� 3�_�u
        public User user;
        public int which = -1; // �L���ؼб���(�u�����R�Ψ�)
        public int whom = -1;// �L�N�쪺�ؼЪ��a �L�ޯ઺�ؼ�

        public String getName() {
            return "%%%���a" + seat + "%%%";
        }

        public boolean isOnline() {
            return status != 3;
        }

        public void print(ArrayList<String> mess) {
            for (String ms : mess) {
                user.pw.println(ms);
                System.out.println("�V " + seat + " (" + chr + ") �ǰe�T���G" + ms);
            }
            user.pw.flush();
        }

        public void print(String ms) {
            user.pw.println(ms);
            System.out.println("�V " + seat + " (" + chr + ") �ǰe�T���G" + ms);
            user.pw.flush();
        }

        public void resetForNewGame() {
            chr = null;
            seat = -1;
            isChrCov = false;
            isIdyCov = true;
            idy = 0; // 1��� 2�x�� 3��o 0�L����
            isAlive = 1; // 1��ܦs�� 2���` 3����
            status = 1; // 1�b�u 2�U�� 3�_�u
            hc.clear(); // ��P�N��
            itl.clear(); // �����N��
            ltd = 0; // 0�L 1�Q��w 2�Q�ժ� 3�I�򪬺A
            skills.clear();
            isReady = false;
            sit = 0; // "��Hetc" //1��H 2�ժ� 3�ձ� 4�N�H 5�N�d 6�ѯ}
            cardId = 0; // �L���b�ϥΪ��d�P;
            whom = -1;// �L�N�쪺�ؼЪ��a �L�ޯ઺�ؼ�
            which = -1; // �L���ؼб���(�u�����R�Ψ�)
            hasPrepareOldSkill = false;
        }

        public void setRoom(Room r) {
            this.room = r;
            user.room = r;
        }
    }

    class Room {

        public Room(int id) {
            this.id = id;
            for (int i = 0; i < 9; i++) {
                waitingplayers.add(null);
                isBlocked.add(false);
            }
            PipedOutputStream out = new PipedOutputStream();
            pout = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out)));
            try {
                pin = new BufferedReader(new InputStreamReader(new PipedInputStream(out)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        class MessageTimer extends Thread {

            public MessageTimer() {}
            public MessageTimer(int who, String mess, int time, String check) {
                this.who = who;
                this.mess = mess;
                this.time = time;
                this.check = check;
            }
            public MessageTimer(int who, String mess, int time, String check,
                                boolean shouldInterrupted) {
                this.who = who;
                this.mess = mess;
                this.time = time;
                this.itr = shouldInterrupted;
                this.check = check;
            }
            String check;
            boolean itr = true;
            String mess;
            int time;
            int who = -1;

            public String get() {
                String readed = null;
                while (true) {
                    try {
                        readed = pin.readLine();
                        if (readed == null)
                            return null;
                        String[] r0 = readed.split("\\$");
                        int whom0 = Integer.parseInt(r0[0]);
                        if (who != whom0)
                            new LogicException(
                                    "���~�����a:���O " + who + ", ���� " + whom0 + ": " + readed)
                                    .printStackTrace();
                        String[] reads = r0[1].split(":");
                        if (reads[0].equals(check)) {
                            synchronized (stage) {
                                if (itr) {
                                    interrupt();
                                    print(who, "����ʧ@");
                                }
                            }
                            if (reads.length > 1)
                                return reads[1];
                            else
                                return null;
                        }
                        else
                            new LogicException("�����T���^��: " + readed + ", check= " + check)
                                    .printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }

            public void replyNow() {
                interrupt();
                pout.println(who + "$" + mess);
                pout.flush();
            }

            @Override
            public void run() {
                try {
                    Thread.sleep(time + wtplus);
                    pout.println(who + "$" + mess);
                    pout.flush();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        class OldSkill {
            public OldSkill(int p, int id) {
                this.p = p;
                skillId = id;
            }
            int p, skillId;
        }
        public static final int wt = 8000;
        public static final int wtplus = 3500; // ���ݩ���ɶ�
        public ArrayList<String> $chrs; // ����P���|��A����n�I�s�N�q�o��
        public ArrayList<Integer> $dier = new ArrayList<>();
        private String $key; // ��ť�Ҧ��H�O�_�^�_���ؼаT��
        private ArrayList<Integer> $rc = new ArrayList<Integer>(); // �P�_�Ҧ��H���^�F�T���F�S
        public MessageTimer ask_th = new MessageTimer();
        public int at; // �����ǻ���F���a
        public boolean canUseCard; // �{�b�O�_�i�εP
        public int chief = -1; // UID
        WinException e = new WinException();
        public HashSet<Player> hasBeenLocked = new HashSet<>(); // ��"�^�X"�Q��w�L���H���W��
        public HashSet<Player> hasLocked = new HashSet<>(); // ��"�^�X"�ϥιL��w���H���W��
        public boolean hasSeen = false; // ��"���q"�O�_�w���d�P�Q�ѯ}
        public boolean hasTackled = false; // ��"�^�X"�_�w�����a�I�򱡳�
        public boolean hasTested = false; // ��"���q"�^�X�D���a�O�_�w�g�ιL�ձ�
        public int id;
        public boolean isBack;
        public ArrayList<Boolean> isBlocked = new ArrayList<>();
        public boolean isItlCov; // �����O�_�\��
        public int itl; // �ǻ����������N��
        public String itlType;
        public boolean itlWay; // �����ǻ���V�Atrue��ܦV�k��
        public ArrayList<Integer> mt; // �P�w
        public String name = "�ũ�";
        public BufferedReader pin;
        public ArrayList<Player> players = new ArrayList<>(); // ���a���
        public int plyCount = 0; // ���a�`��
        public PrintWriter pout;
        public int pri = 0; // ��e�^�X���a
        public ArrayList<String> queue = new ArrayList<>(); // �ݵ��⪺�d�P
        public OldSkill[] queueOldSkill;
        public Thread roomThread;
        public String stage = "waiting"; // �C�����q: chochr, ini, start, I, choitl,
        public ArrayList<Integer> testTrash = new ArrayList<>(18); // �ձ���P��
        public Thread time_th;
        public boolean timeflag;
        public ArrayList<Integer> trash = new ArrayList<>(81); // ��P��
        public int ttt; // �p�ɮɶ�
        Runnable useCard_run = () -> {
            try {
                Thread.sleep(wt + wtplus);
                synchronized (stage) {
                    for (Player pl : getAliveOnlinePlayers()) {
                        if (!hasCalled(pl.seat, "���X�P")) {
                            pout.println(pl.seat + "$�ϥΤ�P:-1");
                            pout.flush();
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        public ArrayList<Player> waitingplayers = new ArrayList<>(); // ���a���
        int writeenddead = 0;

        public boolean askUseSkill(Player p, int skillId) {
            if (p.status != 1)
                return false;
            print(p.seat, "�ޯ�]�w:" + skillId + ":1:" + wt);
            print(p.seat, "������r:�O�_�o�ʧޯ� \"" + Skill.getTxt(skillId) + " \"�H");
            printEx(p.seat, "������r:����" + p.getName() + "�ާ@");
            ask_th = new MessageTimer(p.seat, "�ϥΧޯ�:-1", wt, "�ϥΧޯ�");
            ask_th.start();
            return skillId == Integer.parseInt(ask_th.get());
        }

        public void calculate() throws WinException, NoPlayersException {
            // ��������� TODO Ĳ�o�ޯ�n�ɤW��~!
            print("�}�l����");
            int size = queue.size();
            for (int i = size - 1; i >= 0; i--) {
                // ��:id:�d�P����:whom:which
                // -1:id(��L��)
                String[] q = queue.get(i).split(":");
                int p = Integer.parseInt(q[0]);
                int id = Integer.parseInt(q[1]);
                if (p == -1) {
                    // �Q�ѯ}�B�I��L�ĵ���
                    trash.add((Integer) id);
                    print("�����d�P:" + i);
                }
                else {

                    if (Integer.parseInt(q[3]) == -1)
                        useSkillWhenCal(id, p, 0, q[2], i);
                    else
                        useSkillWhenCal(id, p, Integer.parseInt(q[3]), q[2], i);

                    if (id < 100 && id > 0) { // �d�P

                        switch (q[2]) {
                        case "��w":
                            trash.add((Integer) id);
                            int whom = Integer.parseInt(q[3]);
                            if (players.get(p).isAlive == 1 && players.get(whom).isAlive == 1) {
                                players.get(whom).ltd = 1;
                                print("���a���A:" + whom + ":1:true");
                            }
                            print("�����d�P:" + i);

                            break;
                        case "�ժ����s":
                            whom = Integer.parseInt(q[3]);
                            trash.add((Integer) id);
                            if (players.get(p).isAlive == 1 && players.get(whom).isAlive == 1) {
                                if (players.get(whom).ltd != 1 && players.get(whom).ltd != 3)
                                    players.get(whom).ltd = 2;
                                print("���a���A:" + whom + ":2:" + (players.get(whom).ltd != 1 &&
                                        players.get(whom).ltd != 3));
                            }
                            print("�����d�P:" + i);

                            break;
                        case "�I��":
                            trash.add((Integer) id);
                            if (players.get(p).isAlive == 1) {
                                for (int j = 0; j < i; j++) {
                                    String[] r = queue.get(j).split(":");
                                    if (r[2].equals("�I��"))
                                        queue.set(j, "-1:" + r[1] + ":" + r[2]);
                                }
                                players.get(p).ltd = 3;
                                at = p;
                                print("���a���A:" + p + ":3:true");
                                print("��������:" + p);
                            }
                            print("�����d�P:" + i);
                            // TODO
                            break;
                        case "�}Ķ":
                            // TODO
                            if (players.get(p).status == 1 && players.get(p).isAlive == 1) {
                                if (CardData.isTrue(itl))
                                    print(p, "�[�ݥd�P:" + itl + ":����O�ǻ���������");
                                else
                                    print(p, "�[�ݥd�P:" + itl + ":����O�ǻ����������A�����A�O�A�~�٤ӭ�");
                                printEx(p, "������r:����" + players.get(p).getName() + "�˵�����");
                                ask_th = new MessageTimer(p, "�[�ݵ���", wt, "�[�ݵ���");
                                ask_th.start();
                                ask_th.get();
                                print(p, "����ʧ@");
                                print("������r:-1");
                            }
                            print("�����d�P:" + i);
                            trash.add((Integer) id);
                            break;
                        case "�h�^":
                            // TODO
                            trash.add((Integer) id);
                            if (players.get(p).isAlive == 1) {
                                print("���a���A:" + p + ":4:" + (players.get(p).ltd == 0 || players.get(
                                        p).ltd == 4));
                                if (players.get(p).ltd == 0 || players.get(p).ltd == 4) {
                                    players.get(p).ltd = 4;
                                    isBack = true;
                                }
                            }
                            print("�����d�P:" + i);
                            break;
                        case "�N��":
                            whom = Integer.parseInt(q[3]);
                            if (players.get(p).isAlive == 1 && players.get(whom).isAlive == 1) {
                                int targetId = Integer.parseInt(q[4]);
                                if (players.get(whom).itl.contains(targetId)) {
                                    players.get(whom).itl.remove((Integer) targetId);
                                    String col = CardData.getCardColor(targetId);
                                    int cc = CardData.getColorCount(players.get(whom).itl, col);
                                    print("��@�P�ʵe:" + targetId + ":" + whom + ":" + col + ":" + cc);
                                    print("�����d�P:" + i);
                                    trash.add(targetId);
                                }
                            }
                            trash.add((Integer) id);
                            // TODO �X���s
                            break;
                        case "�ձ�":
                            whom = Integer.parseInt(q[3]);
                            if (players.get(p).isAlive == 1 && players.get(whom).isAlive == 1) {
                                int idy = -1, c = 0;
                                if (id > 0 && id <= 9) {
                                    idy = (id - 1) / 3 + 1;
                                    c = 2;
                                }
                                else if (id > 9 && id <= 18) {
                                    idy = (id - 10) / 3 + 1;
                                    c = -1;
                                }
                                int light = -1;
                                if (players.get(whom).chr.equals("�Ѱ�") && !players.get(whom).isChrCov)
                                    light = 3;
                                else
                                    light = players.get(whom).idy == idy ? 1 : 2;
                                if (players.get(whom).status == 1) {
                                    print(whom, "�i��ձ�:" + id + ":" + idy + ":" + c + ":" + light);
                                    printEx(whom,
                                            "������r:����" + players.get(whom).getName() + "�^���ձ�");
                                    ask_th = new MessageTimer(whom, "�^���ձ�:" + light, wt, "�^���ձ�");
                                    ask_th.start();
                                    light = Integer.parseInt(ask_th.get());
                                    print(whom, "�ձ�����");
                                    printEx(whom, "������r:-1");
                                    if (light == -1) {
                                        if (players.get(whom).chr.equals("�Ѱ�") && !players.get(
                                                whom).isChrCov)
                                            light = c == 2 ? 1 : 2;
                                        else
                                            light = players.get(whom).idy == idy ? 1 : 2;
                                    }
                                    listening();
                                }
                                else {
                                    if (players.get(whom).chr.equals("�Ѱ�") && !players.get(
                                            whom).isChrCov)
                                        light = c == 2 ? 1 : 2;
                                    else
                                        light = players.get(whom).idy == idy ? 1 : 2;
                                } // TODO�Ѱ�
                                if (players.get(whom).chr.equals("�Ѱ�") && !players.get(whom).isChrCov) {
                                    print("������r:-1");
                                    print("�ޯ�ʵe:�Ѱ�:����:-1:-1:-1");
                                }
                                else
                                    printEx(whom, "������r:-1");
                                if (light == 1) {
                                    if (c == 2) {
                                        int h1 = -1, h2 = -1;
                                        try {
                                            h1 = drawcard();
                                            try {
                                                h2 = drawcard();
                                                players.get(whom).hc.add(h1);
                                                players.get(whom).hc.add(h2);
                                                print("��P�ʵe:" + whom + ":2:" + players.get(whom).hc
                                                        .size());
                                                print(whom, "�W�h��P:" + h1 + "," + h2 + ":-1");
                                            } catch (NoCardException e) {
                                                players.get(whom).hc.add(h1);
                                                print("�}�B��r:" + players.get(whom).getName() +
                                                              "�n�٩��i�P");
                                                print("��P�ʵe:" + whom + ":1:" + players.get(whom).hc
                                                        .size());
                                                print(whom, "�W�@��P:" + h1 + ":-1");
                                            }
                                            checkWinByHc(players.get(pri));
                                        } catch (NoCardException e) {
                                            print("�}�B��r:" + players.get(whom).getName() +
                                                          "�n�٩��i�P");
                                        }
                                    }
                                    else if (c == -1) {
                                        if (players.get(whom).hc.isEmpty())
                                            print("�}�B��r:" + players.get(whom).getName() +
                                                          "�n�ٱ�@�i��P");
                                        else {
                                            ask_th = new MessageTimer(whom, "��ܤ�P:-1", wt, "��ܤ�P");
                                            ask_th.start();

                                            String hcms = "";
                                            for (int hcid : players.get(whom).hc)
                                                hcms += hcid + ",";
                                            print(whom, "��@�i��P:" + hcms + ":true:��ܤ@�i�n��󪺤�P");
                                            printEx(whom, "������r:���ݪ��a��ܱ�P");
                                            int idt = Integer.parseInt(ask_th.get());
                                            print(whom, "����ʧ@");
                                            print("������r:-1");
                                            if (idt == -1)
                                                idt = players.get(whom).hc
                                                        .get(new Random().nextInt(
                                                                players.get(whom).hc.size()));
                                            players.get(whom).hc.remove((Integer) idt);
                                            trash.add(idt);
                                            print(whom, "�R�@��P:" + idt + ":-1");
                                            print("��@�P�ʵe:" + idt + ":" + whom + ":h:" + players.get(
                                                    whom).hc.size());
                                        }
                                    }
                                }
                                else if (light == 2) { // �^���D��
                                    if (c == 2) {
                                        print("�}�B��r:" + players.get(whom).getName() +
                                                      "�n�� \"���ڬO�ש�\"");
                                        print("�����n��:" + "�y��/" +
                                                      (CharacterData.isMaleBySound(players.get(whom)) ?
                                                              "�k_���ڬO�ש�"
                                                              : "�k_���ڬO�ש�"));
                                    }
                                    else if (c == -1) {
                                        print("�}�B��r:" + players.get(whom).getName() +
                                                      "�n�� \"�ڬO�@�Ӧn�H\"");
                                        print("�����n��:" + "�y��/" +
                                                      (CharacterData.isMaleBySound(players.get(whom)) ?
                                                              "�k_�ڬO�@�Ӧn�H"
                                                              : "�k_�ڬO�@�Ӧn�H"));
                                    }
                                    else
                                        new LogicException("���~�� c: " + c).printStackTrace();
                                }
                                else
                                    new LogicException("���~�� light: " + light).printStackTrace();
                            }
                            print("�����d�P:" + i);
                            testTrash.add((Integer) id);
                            // TODO �`�N�Ѱ�
                            break;
                        case "�u������":
                            if (players.get(p).isAlive == 1) {
                                int t = getAlivePlayersCount();
                                ArrayList<Integer> lotts = new ArrayList<>();
                                int ii = 0;
                                try {
                                    for (ii = 0; ii < t; ii++)
                                        lotts.add(drawcard());
                                } catch (NoCardException e) {
                                    // �P�w�L�P
                                    t = ii + 1;
                                    e.printStackTrace();
                                }
                                ArrayList<Integer> _l = new ArrayList<>(lotts);
                                int[] random = Shuffle.getRandom(t, t);
                                for (ii = 0; ii < t; ii++)
                                    lotts.set(ii, _l.get(random[ii]));
                                int g = pri;
                                print("�u���}�l:" + t);
                                boolean flag = true;
                                for (int lott : lotts) {
                                    if (flag) {
                                        String color = CardData.getCardColor(lott);
                                        players.get(g).itl.add((Integer) lott);
                                        int cc = CardData.getColorCount(players.get(g).itl, color);
                                        String sound1 = null, sound2 = null;
                                        if (color.equals("k")) {
                                            sound1 = "�y��/" + (CharacterData.isMale(players.get(g))
                                                    ? "�k_������_" + (new Random().nextInt(3) + 1)
                                                    : "�k_������_" + (new Random().nextInt(2) + 1));
                                            sound2 = "������";
                                        }
                                        else
                                            sound1 = "�y��/" + (CharacterData.isMale(players.get(g)) ?
                                                    "�k_�u����_" : "�k_�u����_")
                                                    + (new Random().nextInt(3) + 1);
                                        if (sound2 == null)
                                            print("�u���o�P:" + lott + ":" + g + ":" + color + ":" + cc +
                                                          ":" + sound1);
                                        else
                                            print("�u���o�P:" + lott + ":" + g + ":" + color + ":" + cc +
                                                          ":" + sound1 + ":"
                                                          + sound2);
                                        checkWinOrDead(p, g, false, lott);
                                        g = getNextPlayer(g, true);
                                        if (g == pri)
                                            flag = false;
                                    }
                                    else
                                        trash.add(lott);
                                }
                                print("�u������");
                            }
                            print("�����d�P:" + i);
                            trash.add((Integer) id);
                            // TODO
                            break;
                        case "�ѯ}":
                            if (players.get(p).isAlive == 1) {
                                int targetId = Integer.parseInt(q[4]);
                                for (int j = 0; j < i; j++) {
                                    String[] rs = queue.get(j).split(":");
                                    if (Integer.parseInt(rs[1]) == targetId) {
                                        queue.set(j, "-1:" + rs[1] + ":" + rs[2] + ":-1:-1");
                                        break;
                                    }
                                }
                            }
                            print("�����d�P:" + i);
                            trash.add((Integer) id);
                            break;
                        default:
                            new LogicException("�����T���d�Ptype: " + q[2]).printStackTrace();
                            return;
                        }
                    }
                    else if (id < 200) {
                        if (players.get(p).isAlive == 1)
                            // �ޯ�
                            switch (id) {
                            case 104: // �X��
                                int whom = Integer.parseInt(q[3]);
                                if (players.get(whom).isAlive == 1) {
                                    players.get(whom).isChrCov = true;
                                    print("�\�񨤦�:" + whom + ":" + players.get(whom).chr);
                                }
                                break;
                            case 107: // �tĶ
                                if (itl != -1) { // �I�W�I���ᤣ��tĶ
                                    ask_th = new MessageTimer(p, "�[�ݵ���", wt, "�[�ݵ���");
                                    if (CardData.isTrue(itl))
                                        print(p, "�[�ݥd�P:" + itl + ":����O�ǻ���������");
                                    else
                                        print(p,
                                              "�[�ݥd�P:" + itl + ":����O�ǻ����������A�����I�O�A�~�٤ӭ�");
                                    printEx(p, "������r:����" + players.get(p).getName() + "�˵�����");
                                    ask_th.start();
                                    ask_th.get();
                                    print(p, "�[�ݵ���");
                                    print("������r:-1");
                                }
                                try {
                                    int h = drawcard();
                                    players.get(p).hc.add(h);
                                    print("��P�ʵe:" + p + ":1:" + players.get(p).hc.size());
                                    print(p, "�W�@��P:" + h + ":-1");
                                } catch (NoCardException e1) {}
                                break;
                            case 109: // ���å���
                                whom = Integer.parseInt(q[3]);
                                if (players.get(whom).isAlive == 1) {
                                    int which = Integer.parseInt(q[4]);
                                    if (players.get(whom).itl.contains(which)) {
                                        players.get(whom).itl.remove((Integer) which);
                                        mt.add(0, which);
                                        String loc = CardData.getCardColor(which);
                                        print("�ʤ@�P�ʵe:" + which + ":" + whom + ":" + loc + ":"
                                                      + CardData.getColorCount(players.get(whom).itl,
                                                                               loc) +
                                                      ":1000:-1:-1:-1:-1");
                                    }
                                }
                                break;
                            case 111: // ��V����
                                whom = Integer.parseInt(q[3]);
                                if (players.get(whom).isAlive == 1) {
                                    int c = mt.get(0);
                                    print("�ʤ@�P�ʵe:" + c + ":1001:-1:-1:1000:-1:-1:-1:-1");
                                    if (!players.get(p).hc.isEmpty()) {
                                        wait(players.get(p));
                                        if (CardData.isTrue(c)) { // ��@�i
                                            print(p, "��@�i��P:" +
                                                    CardData.getCardsByColor(players.get(p).hc, "k")
                                                    + ":false:��ܭn��m���d�P:-1");
                                            int h = -1;
                                            ask_th = new MessageTimer(p, "��ܤ�P:-1", wt, "��ܤ�P");
                                            ask_th.start();
                                            h = Integer.parseInt(ask_th.get());
                                            print(p, "����ʧ@");
                                            print("������r:-1");
                                            if (h != -1) {
                                                players.get(p).hc.remove((Integer) h);
                                                players.get(whom).itl.add(h);
                                                print(p, "�R�@��P:" + h + ":-1");
                                                String sound;
                                                if (CharacterData.isMaleBySound(players.get(whom)))
                                                    sound = "�y��/�k_������_" + (new Random().nextInt(
                                                            3) + 1);
                                                else
                                                    sound = "�y��/�k_������" + (new Random().nextInt(3) +
                                                            2);
                                                print("�ʤ@�P�ʵe:" + h + ":" + p + ":h:" + players.get(
                                                        p).hc.size() + ":" + whom
                                                              + ":k:" + CardData.getColorCount(
                                                        players.get(whom).itl, "k")
                                                              + ":������:" + sound);
                                                checkWinOrDead(p, whom, false, h);
                                            }
                                        }
                                        else { // ���f
                                            print(p, "��h�i��P:" +
                                                    CardData.getCardsByColor(players.get(p).hc, "k")
                                                    + ":false:0:3:12000:��ܭn��m���d�P");
                                            wait(players.get(p));
                                            ask_th = new MessageTimer(p, "��ܦh�i��P:-1", 12000,
                                                                      "��ܦh�i��P");
                                            ask_th.start();
                                            String hs = ask_th.get();
                                            print(p, "����ʧ@");
                                            print("������r:-1");
                                            if (!hs.equals("-1")) {
                                                String[] $hs = hs.split(",");

                                                int[] $h = new int[$hs.length];
                                                for (int a = 0; a < $h.length; a++) {
                                                    $h[a] = Integer.parseInt($hs[a]);
                                                    players.get(p).hc.remove((Integer) $h[a]);
                                                    players.get(whom).itl.add($h[a]);
                                                }
                                                print(p, "�R�h��P:" + hs + ":-1");
                                                String sound;
                                                if (CharacterData.isMaleBySound(players.get(whom)))
                                                    sound = "�y��/�k_������_" + (new Random().nextInt(
                                                            3) + 1);
                                                else
                                                    sound = "�y��/�k_������" + (new Random().nextInt(3) +
                                                            2);
                                                String mess = "�ʦh�P�ʵe:";
                                                for (int a = 0; a < $h.length; a++)
                                                    mess += $h[a] + ",";
                                                mess += ":";
                                                for (int a = 0; a < $h.length; a++)
                                                    mess += p + ",";
                                                mess += ":";
                                                for (int a = 0; a < $h.length; a++)
                                                    mess += "h,";
                                                mess += ":";
                                                size = players.get(p).hc.size();
                                                for (int a = 0; a < $h.length; a++)
                                                    mess += size + ",";
                                                mess += ":";
                                                for (int a = 0; a < $h.length; a++)
                                                    mess += whom + ",";
                                                mess += ":";
                                                for (int a = 0; a < $h.length; a++)
                                                    mess += "k,";
                                                mess += ":";
                                                size = CardData.getColorCount(players.get(whom).itl,
                                                                              "k");
                                                for (int a = 0; a < $h.length; a++)
                                                    mess += size + ",";
                                                mess += ":������:" + sound;
                                                print(mess);
                                                checkWinOrDead(p, whom, false, $h);
                                            }
                                        }
                                    }
                                }
                                break;
                            case 112: // ĵı
                                int targetId = Integer.parseInt(q[4]);
                                for (int j = 0; j < i; j++) {
                                    String[] rs = queue.get(j).split(":");
                                    int id1 = Integer.parseInt(rs[1]);
                                    if (id1 == targetId) {
                                        queue.set(j, "-1:" + rs[1] + ":" + rs[2] + ":-1:-1");
                                        break;
                                    }
                                }
                                break;
                            case 115: // ����
                                whom = Integer.parseInt(q[3]);
                                if (players.get(whom).isAlive == 1) {
                                    size = players.get(whom).hc.size();
                                    int[] ihs = null;
                                    String hs = "";
                                    String whos = "";
                                    String whoms = "";
                                    String locs = "";
                                    String cs0 = "";
                                    String cs1 = "";
                                    String cards = "";
                                    if (size < 4 && size > 0) {
                                        ihs = new int[size];
                                        for (int ih = 0; ih < ihs.length; ih++) {
                                            ihs[ih] = players.get(whom).hc.get(ih);
                                            players.get(p).hc.add(ihs[ih]);
                                        }
                                        players.get(whom).hc.clear();
                                    }
                                    else {
                                        ihs = new int[3];
                                        int[] $h = Shuffle.getRandom(3, size);
                                        ihs[0] = players.get(whom).hc.get($h[0]);
                                        ihs[1] = players.get(whom).hc.get($h[1]);
                                        ihs[2] = players.get(whom).hc.get($h[2]);
                                        players.get(whom).hc.remove((Integer) ihs[0]);
                                        players.get(whom).hc.remove((Integer) ihs[1]);
                                        players.get(whom).hc.remove((Integer) ihs[2]);
                                        players.get(p).hc.add(ihs[0]);
                                        players.get(p).hc.add(ihs[1]);
                                        players.get(p).hc.add(ihs[2]);
                                    }
                                    for (int ih = 0; ih < ihs.length; ih++) {
                                        cards += ihs[ih] + ",";
                                        hs += "0,";
                                        whos += p + ",";
                                        locs += "h,";
                                        cs0 += players.get(p).hc.size() + ",";
                                        cs1 += "0,";
                                        whoms += whom + ",";
                                    }
                                    print(whom, "�R�h��P:" + cards + ":-1");
                                    print("�ʦh�P�ʵe:" + hs + ":" + whoms + ":" + locs + ":" + cs1 + ":" +
                                                  whos + ":" + locs
                                                  + ":" + cs0 + ":-1:-1");
                                    print(p, "�W�h��P:" + cards + ":-1");
                                    try {
                                        int _h = drawcard();
                                        players.get(whom).hc.add(_h);
                                        print("��P�ʵe:" + whom + ":1:" + players.get(whom).hc.size());
                                        print(whom, "�W�@��P:" + _h + ":-1");
                                        checkWinByHc(players.get(whom));
                                    } catch (NoCardException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            case 117: // �I�W�I
                                players.get(p).isChrCov = false;
                                players.get(p).itl.add(itl);
                                String col = CardData.getCardColor(itl);
                                String sound = CardData.isTrue(itl) ?
                                        "�y��/�k_�u����_" + (new Random().nextInt(3) + 1)
                                        : "�y��/�k_������_" + (new Random().nextInt(3) + 1);
                                print("��������:" + isItlCov + ":" + itl + ":" + p + ":" + col + ":"
                                              + CardData.getColorCount(players.get(p).itl, col) + ":" +
                                              sound);
                                itl = -1;
                                break;
                            case 122: // �R��
                                String pls = "";
                                for (Player $p : getAlivePlayers()) {
                                    if (CardData.getColorCount($p.itl, "k") > 0)
                                        pls += $p.seat + ",";
                                }
                                if (!pls.equals("")) {
                                    print(p, "��@�쪱�a:" + pls + ":false:��ܲĤ@�쪱�a");
                                    wait(players.get(p));
                                    int whom1 = -1;
                                    ask_th = new MessageTimer(p, "��ܪ��a:-1", wt, "��ܪ��a");
                                    ask_th.start();
                                    whom1 = Integer.parseInt(ask_th.get());
                                    if (whom1 == -1)
                                        print("������r:-1");
                                    else {
                                        print(p, "��h�i����:" +
                                                CardData.getCardsByColor(players.get(whom1).itl, "k")
                                                + ":false:0:2:10000:��ܭn�N��������");
                                        ask_th = new MessageTimer(p, "��ܦh�i����:-1", wt, "��ܦh�i����");
                                        ask_th.start();
                                        String sids = ask_th.get();
                                        if (sids.equals("-1"))
                                            print("������r:-1");
                                        else {
                                            String[] sid = sids.split(",");
                                            int[] ids = new int[2];
                                            if (sid.length == 1) { // �N�@�i
                                                String pls2 = "";
                                                for (Player $p : getAlivePlayers())
                                                    if ($p.seat != whom1 && CardData.getColorCount(
                                                            $p.itl, "k") > 0)
                                                        pls2 += $p.seat + ",";
                                                ids[0] = Integer.parseInt(sid[0]);
                                                if (pls2.equals("")) { // �u�N�@�H
                                                    players.get(whom1).itl.remove((Integer) ids[0]);
                                                    trash.add(ids[0]);
                                                    print("������r:-1");
                                                    print("�ʤ@�P�ʵe:" + ids[0] + ":" + whom1 + ":k:"
                                                                  + CardData.getColorCount(
                                                            players.get(whom1).itl, "k")
                                                                  + ":999:-1:-1:-1:-1");
                                                }
                                                else {
                                                    boolean cont;
                                                    print(p, "�O�_�~��:�O�_�n�n�~��N���ĤG�쪱�a�H:5000");
                                                    ask_th = new MessageTimer(p, "����~��:false", wt,
                                                                              "����~��");
                                                    ask_th.start();
                                                    cont = Boolean.parseBoolean(ask_th.get());
                                                    if (cont) { // �~��N�ĤG�H
                                                        print(p, "��@�쪱�a:" + pls2 + ":false:��ܲĤG�쪱�a");
                                                        ask_th = new MessageTimer(p, "��ܪ��a:-1", wt,
                                                                                  "��ܪ��a");
                                                        ask_th.start();
                                                        int whom2 = Integer.parseInt(ask_th.get());
                                                        if (whom2 == -1) {
                                                            players.get(whom1).itl.remove(
                                                                    (Integer) ids[0]);
                                                            trash.add(ids[0]);
                                                            print("������r:-1");
                                                            print("�ʤ@�P�ʵe:" + ids[0] + ":" + whom1 +
                                                                          ":k:"
                                                                          + CardData.getColorCount(
                                                                    players.get(whom1).itl, "k")
                                                                          + ":999:-1:-1:-1:-1");
                                                        }
                                                        else {
                                                            ids[1] = -1;
                                                            print(p, "��@�i����:"
                                                                    + CardData.getCardsByColor(
                                                                    players.get(whom2).itl, "k")
                                                                    + ":false:��ܭn�N��������");
                                                            ask_th = new MessageTimer(p, "��ܱ���:-1", wt,
                                                                                      "��ܱ���");
                                                            ask_th.start();
                                                            ids[1] = Integer.parseInt(ask_th.get());
                                                            if (ids[1] ==
                                                                    -1) { //
                                                                    // �S����ܿN�����ĤG�������A�ҥH����N�@�i���ʵe
                                                                players.get(whom1).itl.remove(
                                                                        (Integer) ids[0]);
                                                                trash.add(ids[0]);
                                                                print("������r:-1");
                                                                print("�ʤ@�P�ʵe:" + ids[0] + ":" +
                                                                              whom1 + ":k:"
                                                                              + CardData.getColorCount(
                                                                        players.get(whom1).itl, "k")
                                                                              + ":999:-1:-1:-1:-1");
                                                            }
                                                            else {
                                                                players.get(whom1).itl.remove(
                                                                        (Integer) ids[0]);
                                                                trash.add(ids[0]);
                                                                players.get(whom2).itl.remove(
                                                                        (Integer) ids[1]);
                                                                trash.add(ids[1]);
                                                                print("������r:-1");
                                                                print("�ʦh�P�ʵe:" + ids[0] + "," +
                                                                              ids[1] + ":" + whom1 + ","
                                                                              + whom2 + ":k,k:"
                                                                              + CardData.getColorCount(
                                                                        players.get(whom1).itl, "k")
                                                                              + ","
                                                                              + CardData.getColorCount(
                                                                        players.get(whom2).itl, "k")
                                                                              +
                                                                              ":999,999:-1,-1:-1," +
                                                                               "-1:-1:-1");
                                                            }
                                                        }
                                                    }
                                                    else { // ��ܤ��N�ĤG�H
                                                        players.get(whom1).itl.remove((Integer) ids[0]);
                                                        trash.add(ids[0]);
                                                        print("������r:-1");
                                                        print("�ʤ@�P�ʵe:" + ids[0] + ":" + whom1 + ":k:"
                                                                      + CardData.getColorCount(
                                                                players.get(whom1).itl, "k")
                                                                      + ":999:-1:-1:-1:-1");
                                                    }
                                                }
                                            }
                                            else { // �N��i
                                                for (int d = 0; d < 2; d++)
                                                    ids[d] = Integer.parseInt(sid[d]);
                                                players.get(whom1).itl.remove((Integer) ids[0]);
                                                trash.add(ids[0]);
                                                players.get(whom1).itl.remove((Integer) ids[1]);
                                                trash.add(ids[1]);
                                                print("������r:-1");
                                                print("�ʦh�P�ʵe:" + ids[0] + "," + ids[1] + ":" + whom1 +
                                                              "," + whom1 + ":k,k:"
                                                              + CardData.getColorCount(
                                                        players.get(whom1).itl, "k") + ","
                                                              + CardData.getColorCount(
                                                        players.get(whom1).itl, "k")
                                                              + ":999,999:-1,-1:-1,-1:-1:-1");
                                            }
                                        }
                                    }
                                }
                                break;
                            case 139: // ���S
                                if (isItlCov && itl != -1 && itlType.equals("�K�q")) {
                                    print("�ʤ@�P�ʵe:" + itl + ":301:-1:-1:300:-1:-1:-1:-1");
                                    isItlCov = false;
                                    if (!CardData.isTrue(itl)) {
                                        try {
                                            int h0 = drawcard();
                                            try {
                                                int h1 = drawcard();
                                                players.get(p).hc.add(h0);
                                                players.get(p).hc.add(h1);
                                                print("��P�ʵe:" + p + ":2:" + players.get(p).hc.size());
                                                print(p, "�W�h��P:" + h0 + "," + h1 + ":-1");
                                            } catch (NoCardException e) {
                                                e.printStackTrace();
                                                players.get(p).hc.add(h0);
                                                print("��P�ʵe:" + p + ":1:" + players.get(p).hc.size());
                                                print(p, "�W�@��P:" + h0 + ":-1");
                                            }
                                        } catch (NoCardException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                break;
                            case 144: // ���R
                                id = players.get(p).which;
                                whom = players.get(p).whom;
                                if (players.get(whom).itl.contains(id) && players.get(whom).isAlive ==
                                        1) {
                                    players.get(p).itl.add(id);
                                    players.get(whom).itl.remove((Integer) id);
                                    String loc = CardData.getCardColor(id);
                                    print("�ʤ@�P�ʵe:" + id + ":" + whom + ":" + loc + ":"
                                                  + CardData.getColorCount(players.get(whom).itl, loc) +
                                                  ":" + p + ":" + loc + ":"
                                                  + CardData.getColorCount(players.get(p).itl, loc) +
                                                  ":-1:-1");
                                    checkWinOrDead(p, p, false, id);
                                }
                                break;
                            default:
                                new LogicException("�����T���d�Pi: " + id).printStackTrace();
                                return;
                            }
                        if (id != 117)
                            print("�����d�P:" + i);
                    }
                    else {
                        new LogicException("�����T���d�Pid: " + id).printStackTrace();
                        return;
                    }
                }
                listening();
            }

            queue.clear();
            print("���⵲��");

        }

        public boolean call(int p, String ms) {

            // �^�ǬO�_�Ҧ��H���ǰe��T���F
            if (ms.equals($key)) {
                if (!$rc.contains(p)) {
                    $rc.add(p);
                    for (Player pl : getAliveOnlinePlayers())
                        if (!$rc.contains(pl.seat)) { // �o�{�@�쬡�ۡB�W�u�B�o�٨S�^���T�����H
                            System.out.println("�T���|���^�������A�]�����a" + pl.seat + ":�|���^��");
                            return false;
                        }
                    $key = null;
                    $rc.clear();
                    return true;
                }
                else {
                    System.out.println("���a" + p + "���Ʀ^���F�T��");
                }
            }
            else
                new GameException("���a " + p + " �^���F���~�T��: " + ms + ", ���T�T�����O: " + $key)
                        .printStackTrace();
            return false;
        }

        public boolean canBurn() {
            for (Player pl : players) {
                if (pl.isAlive == 1)
                    for (int id : pl.itl)
                        if (CardData.getCardColor(id).equals("k"))
                            return true;
            }
            return false;
        }

        public boolean canSee() {
            // ����k�ΨӧP�_�ѯ}�έ��ƺI�򪺮ĤO�A�ǰe�f���d�P��Ƹs
            for (String q : queue) {
                if (Integer.parseInt(q.split(":")[1]) < 100) {
                    return true;
                }
            }
            return false;
        }

        public void checkOld(Player p) { // �Ѧr�����Q��½���ޯ�

            if (p.isChrCov && !p.hasPrepareOldSkill) {
                if (p.chr.equals("�Ѱ�")) {
                    for (String qs : getQueueRs()) {
                        String[] q = qs.split(":");
                        if ((q[2].equals("�ձ�") || q[2].equals("��w")) && Integer.parseInt(q[3]) ==
                                p.seat) {
                            print(p.seat, "�ޯ�]�w:100:1");
                            break;
                        }
                    }
                }
                else if (p.chr.equals("�Ѻj")) {
                    for (String qs : getQueueRs()) {
                        String[] q = qs.split(":");
                        if ((q[2].equals("�ձ�") || q[2].equals("��w")) && Integer.parseInt(q[3]) ==
                                p.seat) {
                            print(p.seat, "�ޯ�]�w:103:1");
                            break;
                        }

                    }
                }
                else if (p.chr.equals("�Ѫ�")) {
                    System.out.println("���F�Ѫ�");
                    System.out.println("getQueueRs() = " + getQueueRs());
                    for (String qs : getQueueRs()) {
                        String[] q = qs.split(":");
                        System.out.println(qs + ": " + (q[2].equals("�ѯ}") || Integer.parseInt(q[1]) ==
                                112) + ", "
                                                   + (Integer.parseInt(q[3]) == p.seat));
                        if ((q[2].equals("�ѯ}") || Integer.parseInt(q[1]) == 112) && Integer.parseInt(
                                q[3]) == p.seat) {
                            print(p.seat, "�ޯ�]�w:105:1");
                            break;
                        }
                    }
                }
            }
        }

        public boolean checkSnakeWin() {
            int snake = hasChr("縳D");
            if (snake != -1)
                if (players.get(snake).idy == 3 && getDeadPlayerCount() == 0) {
                    e.wins.clear();
                    e.set(new Win(players.get(snake), true));
                    return true;
                }
            return false;
        }

        public void checkWinByHc(Player p) throws WinException {
            if (p.idy == 3) {
                if (p.chr.equals("�ǵs�E�E")) {
                    if (p.hc.size() > 8)
                        e.set(new Win(players.get(p.seat), true));
                }
                else if (p.chr.equals("���p�U") || p.chr.equals("�Ѫ�")) {
                    if (CardData.getColorCount(p.hc, "r") > 2 && CardData.getColorCount(p.hc, "b") > 2)
                        e.set(new Win(players.get(p.seat), true));
                }
                if (!e.wins.isEmpty()) {
                    checkSnakeWin();
                    checkWinTogether();
                    throw e;
                }
            }
        }

        public void checkWinByIdy() {

            boolean tuchen = false;
            if (getAlivePlayersCount() == 1) { // �O��
                tuchen = true;
                int winidy = getAlivePlayers().get(0).idy;
                if (winidy == 1 || winidy == 2) {
                    for (Player $p : players)
                        if ($p.isAlive != 3 && $p.idy == winidy)
                            e.set(new Win($p, $p.isAlive == 1));
                }
                else // ��o�O��
                    e.set(new Win(getAlivePlayers().get(0), true));
            }
            else { // �٨S�O��

                boolean r = false, b = false, g = false;
                for (Player $p : getAlivePlayers()) {
                    if ($p.idy == 1)
                        r = true;
                    else if ($p.idy == 2)
                        b = true;
                    else if ($p.idy == 3)
                        g = true;
                }

                if (r && !b && !g) {
                    tuchen = true;
                    for (Player $p : getAlivePlayers())
                        if ($p.idy == 1 && $p.isAlive != 3)
                            e.set(new Win($p, $p.isAlive == 1));
                }
                else if (b && !r && !g) {
                    tuchen = true;
                    for (Player $p : getAlivePlayers())
                        if ($p.idy == 2 && $p.isAlive != 3)
                            e.set(new Win($p, $p.isAlive == 1));
                }
            }

            if (tuchen)
                return;

            String[] chrs = {"����", "�p����", "�M�W"};
            for (String chr : chrs) {
                int seat = hasChr(chr);
                if (seat != -1)
                    if (players.get(seat).idy == 3) {
                        boolean dead1 = false, dead2 = false;
                        for (Player pl : players)
                            if (pl.isAlive == 2 && pl.idy == 1) {
                                dead1 = true;
                                break;
                            }
                        for (Player pl : players)
                            if (pl.isAlive == 2 && pl.idy == 2) {
                                dead2 = true;
                                break;
                            }
                        if (dead1 && dead2)
                            e.set(new Win(players.get(seat), true));
                    }
            }

            int white = hasChr("�p��");
            if (white != -1) {
                if (players.get(white).idy == 3) {
                    int dr = 0, db = 0;
                    for (Player pl : players)
                        if (pl.isAlive != 1)
                            if (pl.idy == 1)
                                dr++;
                            else if (pl.idy == 2)
                                db++;
                    switch (players.size()) {
                    case 3:
                    case 4:
                        if (dr == 1 && db == 1)
                            e.set(new Win(players.get(white), true));
                        break;
                    case 5:
                    case 6:
                        if (dr == 2 && db == 2)
                            e.set(new Win(players.get(white), true));
                        break;
                    case 7:
                    case 8:
                    case 9:
                        if (dr == 3 && db == 3)
                            e.set(new Win(players.get(white), true));
                        break;
                    }
                }
            }
        }

        // -----------------------------------------------------------------------

        public void checkWinOrDead(int itlSender, int itlReceiver, boolean passed, int itlId)
                throws WinException, NoPlayersException {
            int[] ids = {itlId};
            checkWinOrDead(itlSender, itlReceiver, passed, ids);
        }

        public void checkWinOrDead(int itlSender, int itlReceiver, boolean passed, int[] itlId)
                throws WinException, NoPlayersException {
            // �����a��o�����ɡA���n�ϥΦ���k
            // who�����ӷ�
            // whom����������
            // 1. �p�տ�
            Player sender = players.get(itlSender);
            Player getter = players.get(itlReceiver);
            if (getter.chr.equals("�p��") && getter.itl.size() > 5) {
                skillWhenGetItl(itl, itlSender, itlReceiver, passed);
                getter.isAlive = 3;
                print("�d�P���f:" + getCardDark());
                print("���a���`:" + itlReceiver + ":�p��:3:" + getter.idy + ":�p�ձ��S");
                listening();
                return; // �L�����U
            }

            // 2.���`�t
            else if (CardData.getColorCount(getter.itl, "k") > 2) {

                int lifu = hasChr("§�A�X���H");
                System.out.println("lifu = " + lifu);
                boolean help = false;
                if (lifu != -1) { // §�A�ϤH
                    if (!players.get(lifu).isChrCov && players.get(lifu).isIdyCov &&
                            CharacterData.isFeMale(getter))
                        if (askUseSkill(players.get(lifu), 129)) {
                            help = true;
                            print(lifu, "����ʧ@");
                            print("������r:-1");
                            players.get(lifu).isIdyCov = false;
                            int k = 0;
                            for (int kid : players.get(lifu).itl)
                                if (!CardData.isTrue(kid))
                                    k++;
                            for (int kid : getter.itl)
                                if (!CardData.isTrue(kid))
                                    k++;
                            int[] ks = new int[k];
                            k = 0;
                            String ids = "";
                            String who = "";
                            String kss = "";
                            String nums = "";
                            String t = "";
                            String minus = "";

                            for (int kid : players.get(lifu).itl)
                                if (!CardData.isTrue(kid)) {
                                    ks[k] = kid;
                                    k++;
                                    ids += kid + ",";
                                    who += lifu + ",";
                                    kss += "k,";
                                    nums += "0,";
                                    t += "1000,";
                                    minus += "-1,";
                                }
                            for (int kid : getter.itl)
                                if (!CardData.isTrue(kid)) {
                                    ks[k] = kid;
                                    k++;
                                    ids += kid + ",";
                                    who += getter.seat + ",";
                                    kss += "k,";
                                    nums += "0,";
                                    t += "1000,";
                                    minus += "-1,";
                                }
                            for (int kid : ks) {
                                players.get(lifu).itl.remove((Integer) kid);
                                getter.itl.remove((Integer) kid);
                            }
                            print("½�}����:" + lifu + ":" + players.get(lifu).idy);
                            print("�ޯ�ʵe:§�A�X���H:�Ϭ�:§�A�X���H�Ϭ�" + (new Random().nextInt(2) + 1) +
                                          ":" + lifu + ":" + getter.seat);
                            print("�ʦh�P�ʵe:" + ids + ":" + who + ":" + kss + ":" + nums + ":" + t +
                                          ":" + minus + ":" + minus
                                          + ":-1:-1");
                            return;
                        }
                        else {
                            print(lifu, "����ʧ@");
                            print("������r:-1");
                        }
                    listening();
                }
                if (!help) { // 3.§�A���ϤH
                    getter.isAlive = 2;
                    $dier.add(itlReceiver);
                    if (sender.idy == 3) { // a.���H��o
                        if (sender.chr.equals("�ª���") && sender.seat != getter.seat) {
                            if (CardData.getColorCount(getter.itl, "�u") > 2)
                                e.set(new Win(sender, true));
                        }
                        else if (sender.chr.equals("¾�~����") && sender.seat != getter.seat) {
                            if (getDeadPlayerCount() > 1)
                                e.set(new Win(sender, true));
                        }
                        else if (sender.chr.equals("�{�F") && sender.seat != getter.seat) {
                            if (CardData.getColorCount(getter.itl, "k") == getter.itl.size())
                                e.set(new Win(sender, true));
                        }
                    }
                    if (getter.idy == 3) { // a.�D����o
                        if (getter.chr.equals("�Ѱ�")) {
                            if (CardData.getColorCount(getter.hc, "r") > 2)
                                e.set(new Win(getter, true));
                        }
                        else if (getter.chr.equals("�B��")) {
                            if (getDeadPlayerCount() > 1)
                                e.set(new Win(getter, true));
                        }
                        else if (getter.chr.equals("�p��")) {
                            if (getDeadPlayerCount() == 1)
                                e.set(new Win(getter, true));
                        }
                    }

                    if (e.wins.isEmpty()) { // �Y�L�H�ŧi�ӧQ�A�}�l�ޯ� TODO
                        print("�d�P���f:" + getCardDark());
                        if (onlyOneTeam() == 0) { // �Ѧh�Ӷ���A�o�ʦ��`�ޯ�
                            System.out.println("�ˬd�I1");
                            skillWhenGetItl(itlId, itlSender, itlReceiver, passed);
                            System.out.println("�ˬd�I2");
                            deadSkill(getter);// b.�ޯ�;
                            // c.�ޯ൲�⧹���A�٬O�S�H�ŧi�ӧQ
                            checkWinByIdy(); // d. 6.�����t
                            if (e.wins.isEmpty()) { // �S�HĹ
                                for (int d : $dier)
                                    print("���a���`:" + d + ":" + players.get(d).chr + ":2:" +
                                                  players.get(d).idy);
                                $dier.clear();
                                listening();
                                return;
                            }
                            else { // ��o���������ӧQ
                                checkWinTogether();
                                for (int d : $dier)
                                    print("���a���`:" + d + ":" + players.get(d).chr + ":2:" +
                                                  players.get(d).idy);
                                $dier.clear();
                                listening();
                                throw e;
                            }
                        }
                        else { // �Ѥ@�Ӷ���A���o�ʦ��`�ޯ�
                            for (int d : $dier)
                                print("���a���`:" + d + ":" + players.get(d).chr + ":2:" +
                                              players.get(d).idy);
                            $dier.clear();
                            checkWinByIdy();
                            checkSnakeWin();
                            checkWinTogether();
                            throw e;
                        }
                    }
                    else { // �D���α��H�ӧQ
                        for (int d : $dier)
                            print("���a���`:" + d + ":" + players.get(d).chr + ":2:" +
                                          players.get(d).idy);
                        $dier.clear();
                        checkWinByIdy(); // 6.
                        checkWinTogether(); // 8.
                        $dier.clear();
                        listening();
                        throw e;
                    }
                }
            }

            // 3.�����t
            else if (getter.idy == 1) {
                if (CardData.getColorCount(getter.itl, "r") > 2)
                    e.set(new Win(getter, true));
            }
            else if (getter.idy == 2) {
                if (CardData.getColorCount(getter.itl, "b") > 2)
                    e.set(new Win(getter, true));
            }
            else if (getter.idy == 3) {
                switch (getter.chr) {
                case "Ķ�q��":
                case "����":
                case "�o�ݭ�":
                    if (CardData.getColorCount(getter.itl, "b") > 2)
                        e.set(new Win(getter, true));
                    break;
                case "�ֺ�����":
                case "�Ѻj":
                case "���K�S�u":
                case "�����B��":
                    if (CardData.getColorCount(getter.itl, "r") > 2)
                        e.set(new Win(getter, true));
                    break;
                case "�m��":
                    if (CardData.getColorCount(getter.itl, "�u") > 4)
                        e.set(new Win(getter, true));
                    break;
                case "���j":
                    if (getter.itl.size() > 5)
                        e.set(new Win(getter, true));
                    break;
                }
            }
            else
                new LogicException("���~������: " + getter.idy).printStackTrace();
            if (e.wins.isEmpty()) {
                listening();
                skillWhenGetItl(itlId, itlSender, itlReceiver, passed);
                return;
            }
            else {

                checkSnakeWin(); // 7.
                checkWinTogether(); // 8.
                for (int d : $dier)
                    print("���a���`:" + d + ":" + players.get(d).chr + ":2:" + players.get(d).idy);
                $dier.clear();
                listening();
                throw e;
            }
        }

        public boolean checkWinOrSkillByIdy() {
            if (getAlivePlayersCount() == 1)
                return true;
            else { // �٨S�O��
                boolean r = false, b = false, g = false;
                for (Player $p : getAlivePlayers()) {
                    if ($p.idy == 1)
                        r = true;
                    else if ($p.idy == 2)
                        b = true;
                    else if ($p.idy == 3)
                        g = true;
                }
                if (r && !b && !g)
                    return true;
                else if (b && !r && !g)
                    return true;
            }
            return false;
        }

        public void checkWinTogether() {
            // who�ŧi�ӧQ��
            int winidy = -1;
            for (Win w : e.wins)
                if (w.sayWin)
                    if (w.idy == 1 || w.idy == 2)
                        winidy = w.idy;
            if (winidy != -1)
                for (Player $p : players)
                    if ($p.idy == winidy && $p.isAlive != 3)
                        e.set(new Win($p, false));
            for (Win win : e.wins)
                if (win.chr.equals("�m��") && win.sayWin) {
                    int i = 0;
                    for (Player f : getAlivePlayers())
                        if (CharacterData.isFeMale(f))
                            i++;
                    Player female = null;
                    if (i == 1) {
                        for (Player f : getAlivePlayers())
                            if (CharacterData.isFeMale(f)) {
                                female = f;
                                break;
                            }
                        print("�ޯ�ʵe:�m��:����:�m�խ���:" + hasChr("�m��") + ":" + female.seat);
                        e.set(new Win(female, false));
                    }
                    else if (i > 1) {
                        String pls = "";
                        for (Player f : getAlivePlayers())
                            if (CharacterData.isFeMale(f))
                                pls += f.seat + ",";
                        print(hasChr("�m��"), "��@�쪱�a:" + pls + ":true:��ܤ@��k�ʦ@�P�ӧQ");
                        wait(hasChr("�m��"));
                        ask_th = new MessageTimer(hasChr("�m��"), "��ܪ��a:-1", wt, "��ܪ��a");
                        ask_th.start();
                        int fe = Integer.parseInt(ask_th.get());
                        if (fe == -1) {
                            ArrayList<Integer> fs = new ArrayList<>();
                            for (Player f : getAlivePlayers())
                                if (CharacterData.isFeMale(f))
                                    fs.add(f.seat);
                            fe = Shuffle.random(fs);
                        }
                        female = players.get(fe);
                        print("�ޯ�ʵe:�m��:����:�m�խ���:" + hasChr("�m��") + ":" + female.seat);
                        e.set(new Win(female, false));
                    }
                    break;
                }

            int lifu = hasChr("§�A�X���H");
            if (lifu != -1)
                if (players.get(lifu).idy == 3 && players.get(lifu).isAlive == 1)
                    for (Win w : e.wins)
                        if (w.sayWin && CharacterData.isFeMale(w.pl)) {
                            e.set(new Win(players.get(lifu), false));
                            break;
                        }

            int beau = hasChr("�j���k");
            if (beau != -1)
                if (players.get(beau).idy == 3 && CardData.getColorCount(players.get(beau).itl, "k") < 2
                        && players.get(beau).isAlive == 1)
                    for (Win w : e.wins)
                        if (w.sayWin && CharacterData.isMale(w.pl)) {
                            e.set(new Win(players.get(beau), false));
                            break;
                        }
        }

        public void choChr() throws StopGameException, NoPlayersException {
            String readed = null;
            while (true) {
                try {
                    readed = pin.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                    if (e.getMessage().equals("Pipe Broken") || e.getMessage().equals(
                            "Write end dead")) {
                        if (plyCount == 0)
                            throw new NoPlayersException();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        continue;
                    }
                    return;
                }
                if (readed != null) {
                    int $ = readed.indexOf("$"), p = -1;
                    // $�e�O�ǰT���̦츹�A��O����
                    if ($ == -1) // �T���~���ǻ�
                        new LogicException("$ = -1").printStackTrace();
                    else
                        p = Integer.parseInt(readed.substring(0, $));
                    String[] line = readed.substring($ + 1).split(":");
                    switch (line[0]) {
                    case "��ܨ���":
                        // ��ܨ���:�B��
                        if (stage.equals("chochr")) { // chochr�~����
                            if (line[1].equals("-1")) // �H�����t����
                                players.get(p).chr = $chrs.get(p * 2 + new Random().nextInt(2));
                            else // Client���w�F����
                                players.get(p).chr = line[1];

                            players.get(p).isChrCov = CharacterData.isCover(players.get(p).chr);
                            ArrayList<String> mess = new ArrayList<>();
                            String pls = "", chrs = "";
                            for (Player pl : players) {
                                if (pl.chr != null) {
                                    // �Ҧ�����T
                                    pls += pl.seat + ",";
                                    chrs += (CharacterData.isCover(pl.chr) ? "����" : pl.chr) + ",";
                                }
                            }
                            mess.add("�����T:" + pls + ":" + chrs);
                            mess.add("�A����T:" + players.get(p).chr + ":" + players.get(p).idy);
                            String skill = "�פJ�ޯ�:";
                            for (int i = 100; i <= Skill.count; i++)
                                if (Skill.getChr(i).equals(players.get(p).chr)) {
                                    skill += i + ":";
                                    players.get(p).skills.add(i);
                                }
                            mess.add(skill);
                            print(p, mess);
                            // �i�D���n����P���o�ӤH�ثe�������p
                            for (Player pl : players)
                                if (pl.chr != null && pl.seat != p) {
                                    print(pl.seat, "�@����T:" + players.get(p).seat + ":"
                                            + (CharacterData.isCover(players.get(p).chr) ? "����" :
                                            players.get(p).chr));
                                }
                            boolean done = true;// �i�D�w��n�P���H���n�P���H����T
                            for (Player pl : players)
                                if (pl.chr == null) {
                                    done = false;
                                    break;
                                }
                            if (done) {// �p�G�Ҧ����a���w�g��n�P�A�~�����U���{���X
                                time_th.interrupt();
                                stage = "ini";
                                return;
                            }
                        }
                        break;
                    case "���a�_�u":
                        players.get(p).user = null;
                        throw new StopGameException();
                    default:
                        new LogicException("����L�k�ѪR���T��: " + readed).printStackTrace();
                        break;
                    }
                }
            }
        }

        public void clearLtd() {

        }

        public void clearRound() {
            clearStage();
            hasLocked.clear();
            hasBeenLocked.clear();
            hasTackled = false;
            itlWay = true;
            itl = -1;
            for (Player pl : players)
                pl.ltd = 0;
        }

        public void clearStage() {
            // �i�J�U�@���q�ɩI�s
            hasSeen = false;
            hasTested = false;
            for (Player pl : players)
                pl.sit = 0;
            isBack = false;
        }

        public void deadSkill(Player dier) throws WinException, NoPlayersException {
            Player p = players.get(pri);
            do {
                System.out.println("���`�ޯ���|: ���a" + p.seat);
                if (p.isAlive == 1 && !p.isChrCov) {
                    if (p.chr.equals("���j") && !dier.hc.isEmpty()) {
                        if (askUseSkill(p, 120)) { // �j��
                            print("�ޯ�ʵe:���j:�j��:���j�j��" + (new Random().nextInt(2) + 1) + ":" +
                                          p.seat + ":" + dier.seat);
                            int h = Shuffle.random(dier.hc);
                            dier.hc.remove((Integer) h);
                            print(p.seat, "�[�ݥd�P:" + h + ":����O�A��쪺�d�P");
                            ask_th = new MessageTimer(p.seat, "�[�ݵ���", wt, "�[�ݵ���");
                            ask_th.start();
                            ask_th.get();
                            print(p.seat, "���X���:���T�w�h�N���P�[�J�����A�_�h�[�J��P");
                            ask_th = new MessageTimer(p.seat, "�ڪ����:false", wt, "�ڪ����");
                            ask_th.start();
                            if (Boolean.parseBoolean(ask_th.get())) {
                                print(p.seat, "����ʧ@");
                                print("������r:-1");
                                p.itl.add(h);
                                print(dier.seat, "�R�@��P:" + h + ":-1");
                                print("�ʤ@�P�ʵe:" + h + ":" + dier.seat + ":h:" + dier.hc.size() + ":" +
                                              p.seat + ":"
                                              + CardData.getCardColor(h) + ":"
                                              + CardData.getColorCount(p.itl, CardData.getCardColor(h)) +
                                              ":-1:-1");
                                checkWinOrDead(p.seat, p.seat, false, h);
                            }
                            else {
                                print(p.seat, "����ʧ@");
                                print("������r:-1");
                                p.hc.add(h);
                                print(dier.seat, "�R�@��P:" + h + ":-1");
                                print("�ʤ@�P�ʵe:0:" + dier.seat + ":h:" + dier.hc.size() + ":" + p.seat +
                                              ":h:"
                                              + CardData.getColorCount(p.hc, CardData.getCardColor(h)) +
                                              ":-1:-1");
                                print(p.seat, "�W�@��P:" + h + ":-1");
                            }
                            listening();
                        }
                        else
                            print("������r:-1");
                    }
                    else if (p.chr.equals("�P�R����")) {
                        if (askUseSkill(p, 124)) {
                            p.idy = dier.idy;
                            dier.idy = 0;
                            dier.isAlive = 3;
                            print(p.seat, "������r:-1");
                            print("�ޯ�ʵe:�P�R����:�{�M���R:�P�R�����{�M���R" +
                                          (new Random().nextInt(2) + 1) + ":" + p.seat + ":"
                                          + dier.seat);
                            print(p.seat, "½�}����:" + p.seat + ":" + p.idy + ":false");
                            boolean canUse = false;
                            for (Player $p : getAlivePlayers())
                                if (!$p.itl.isEmpty()) {
                                    canUse = true;
                                    break;
                                }
                            if (canUse) {
                                printEx(p.seat, "������r:�е���" + p.getName() + ":�ާ@");
                                String pls = "";
                                for (Player $p : getAlivePlayers())
                                    if (!$p.itl.isEmpty())
                                        pls += $p.seat + ",";
                                print(p.seat, "��@�쪱�a:" + pls + ":false:�п�ܭn�󴫱��������a");
                                ask_th = new MessageTimer(p.seat, "��ܪ��a:-1", wt, "��ܪ��a");
                                ask_th.start();
                                int whom = Integer.parseInt(ask_th.get());
                                if (whom != -1) {
                                    print(p.seat,
                                          "��@�i����:" + CardData.getCards(players.get(whom).itl) +
                                                  ":false:�п�ܭn�M�P�w���洫������");
                                    ask_th = new MessageTimer(p.seat, "��ܱ���:-1", wt, "��ܱ���");
                                    ask_th.start();
                                    int id = Integer.parseInt(ask_th.get());
                                    print("������r:-1");
                                    print(p.seat, "����ʧ@");
                                    if (id != -1) {
                                        int tmp = mt.get(0);
                                        players.get(whom).itl.remove((Integer) id);
                                        String idcol = CardData.getCardColor(id);
                                        int sizetmp = CardData.getColorCount(players.get(whom).itl,
                                                                             idcol);
                                        players.get(whom).itl.add(tmp);
                                        mt.set(0, id);
                                        print("�ʦh�P�ʵe:" + id + "," + tmp + ":" + whom + ",1000:" +
                                                      idcol + ",-1:" + sizetmp
                                                      + ",-1:1000," + whom + ":-1," +
                                                      CardData.getCardColor(tmp) + ":-1,"
                                                      + CardData.getColorCount(players.get(whom).itl,
                                                                               CardData.getCardColor(
                                                                                       tmp))
                                                      +
                                                      (CardData.isTrue(tmp) ? ":-1:-1" : ":������:-1"));
                                        checkWinOrDead(p.seat, whom, false, tmp);
                                    }
                                }
                                else
                                    print("������r:-1");
                            }
                        }
                        else
                            print("������r:-1");
                    }
                    else if (p.chr.equals("�M�W")) {
                        if (askUseSkill(p, 131)) {
                            print(p.seat, "������r:-1");
                            print("�ޯ�ʵe:�M�W:�ݦ�:�M�W�ݦ�" + (new Random().nextInt(2) + 1) + ":" +
                                          p.seat + ":" + dier.seat);
                            try {
                                int h0 = drawcard();
                                p.hc.add(h0);
                                try {
                                    int h1 = drawcard();
                                    p.hc.add(h1);
                                    try {
                                        int h2 = drawcard();
                                        p.hc.add(h2);
                                        try {
                                            int h3 = drawcard();
                                            p.hc.add(h3);
                                            print("��P�ʵe:" + p.seat + ":4:" + p.hc.size());
                                            print(p.seat,
                                                  "�W�h��P:" + h0 + "," + h1 + "," + h2 + "," + h3 +
                                                          ":-1");
                                        } catch (NoCardException e) {
                                            e.printStackTrace();
                                            print("��P�ʵe:" + p.seat + ":3:" + p.hc.size());
                                            print(p.seat, "�W�h��P:" + h0 + "," + h1 + "," + h2 + ":-1");
                                        }
                                    } catch (NoCardException e) {
                                        e.printStackTrace();
                                        print("��P�ʵe:" + p.seat + ":2:" + p.hc.size());
                                        print(p.seat, "�W�h��P:" + h0 + "," + h1 + ":-1");
                                    }
                                } catch (NoCardException e) {
                                    e.printStackTrace();
                                    print("��P�ʵe:" + p.seat + ":1:" + p.hc.size());
                                    print(p.seat, "�W�@��P:" + h0 + ":-1");
                                }
                            } catch (NoCardException e) {
                                e.printStackTrace();
                            }
                            if (!p.hc.isEmpty()) {
                                printEx(p.seat, "������r:�е���" + p.getName() + ":�ާ@");
                                print(p.seat, "��@�i��P:" + CardData.getCards(p.hc) +
                                        ":true:��ܤ@�i�n���X�h����P");
                                ask_th = new MessageTimer(p.seat, "��ܤ�P:-1", wt, "��ܤ�P");
                                ask_th.start();
                                int whom = -1;
                                int h = Integer.parseInt(ask_th.get());
                                print(p.seat, "����ʧ@");
                                if (h == -1) {
                                    h = Shuffle.random(p.hc);
                                    whom = p.seat;
                                }
                                else {
                                    String pls = "";
                                    for (Player $p : getAlivePlayers())
                                        pls += $p.seat + ",";
                                    ask_th = new MessageTimer(p.seat, "��ܪ��a:-1", wt, "��ܪ��a");
                                    print(p.seat, "��@�쪱�a:" + pls + ":true:��ܤ@��n����P�����a");

                                    ask_th.start();
                                    whom = Integer.parseInt(ask_th.get());
                                    print(p.seat, "����ʧ@");
                                    if (whom == -1)
                                        whom = p.seat;
                                }
                                print("������r:-1");
                                p.hc.remove((Integer) h);
                                int h0 = p.hc.size();
                                players.get(whom).hc.add(h);
                                int h1 = players.get(whom).hc.size();
                                print("�ʤ@�P�ʵe:0:" + p.seat + ":h:" + h0 + ":" + whom + ":h:" + h1 +
                                              ":-1:-1");
                                listening();
                            }
                            else
                                print("������r:-1");
                        }
                        else
                            print("������r:-1");
                    }
                    else if (p.chr.equals("����") && p.isIdyCov && !p.hc.isEmpty()) {
                        if (askUseSkill(p, 110)) {
                            print(p.seat, "��h�i��P:" + CardData.getCardsByColor(p.hc, "k") +
                                    ":false:0:2:16000:��̦ܳh��i�n��m����P");
                            ask_th = new MessageTimer(p.seat, "��ܦh�i��P:-1", 16000, "��ܦh�i��P");
                            ask_th.start();
                            String cards = ask_th.get();
                            String pls = "";
                            for (Player $p : getAlivePlayers())
                                if ($p.seat != p.seat)
                                    pls += $p.seat + ",";
                            ask_th = new MessageTimer(p.seat, "��ܪ��a:-1", wt, "��ܪ��a");
                            print(p.seat, "��@�쪱�a:" + pls + ":false:��ܤ@�쪱�a");
                            ask_th.start();
                            int whom = Integer.parseInt(ask_th.get());
                            if (whom != -1) {
                                print(p.seat, "����ʧ@");
                                print("������r:-1");
                                if (!cards.equals("-1")) {
                                    String[] cs = cards.split(",");
                                    print("�ޯ�ʵe:����:�����b�Z:���������b�Z:" + p.seat + ":" + whom);
                                    if (cs.length == 1) {
                                        int h = Integer.parseInt(cs[0]);
                                        p.hc.remove((Integer) h);
                                        players.get(whom).itl.add(h);
                                        print(p.seat, "�R�@��P:" + h + ":-1");
                                        print("�ʤ@�P�ʵe:" + h + ":" + p.seat + ":h:" + p.hc.size() +
                                                      ":" + whom + ":k:"
                                                      + CardData.getColorCount(players.get(whom).itl,
                                                                               "k") + ":������:-1");
                                        checkWinOrDead(dier.seat, whom, false, h);
                                        listening();
                                    }
                                    else {
                                        int h0 = Integer.parseInt(cs[0]), h1 = Integer.parseInt(cs[1]);
                                        p.hc.remove((Integer) h0);
                                        p.hc.remove((Integer) h1);
                                        players.get(whom).itl.add(h0);
                                        players.get(whom).itl.add(h1);
                                        int size = CardData.getColorCount(players.get(whom).itl, "k");
                                        print(p.seat, "�R�h��P:" + h0 + "," + h1 + ":-1");
                                        print("�ʦh�P�ʵe:" + h0 + "," + h1 + ":" + p.seat + "," + p.seat +
                                                      ":h,h:" + p.hc.size()
                                                      + "," + p.hc.size() + ":" + whom + "," + whom +
                                                      ":k,k:" + size + "," + size
                                                      + ":������:-1");
                                        int[] hs = {h0, h1};
                                        checkWinOrDead(dier.seat, whom, false, hs);
                                        listening();
                                    }
                                    listening();
                                }
                                else
                                    print("������r:-1");
                            }
                            else
                                print("������r:-1");
                        }
                        else
                            print("������r:-1");
                    }
                }
                else if (p.isAlive == 2 && p.seat == dier.seat && !p.isChrCov) {
                    if (p.chr.equals("�Ѱ�") && !p.hc.isEmpty()) {
                        if (askUseSkill(p, 102)) {
                            String pls = "";
                            for (Player $p : getAlivePlayers())
                                pls += $p.seat + ",";
                            ask_th = new MessageTimer(p.seat, "��ܪ��a:-1", wt, "��ܪ��a");
                            print("��@�쪱�a:" + pls + ":false:��ܤ@�쪱�a");
                            ask_th.start();
                            int whom = Integer.parseInt(ask_th.get());
                            print("������r:-1");
                            if (whom != -1) {
                                print(p.seat, "����ʧ@");
                                print("�ޯ�ʵe:�Ѱ�:���:�Ѱ����:" + p + ":" + whom);
                                if (p.hc.size() < 4) {
                                    String anm = "�ʦh�P�ʵe:";
                                    int[] ids = new int[p.hc.size()];
                                    for (int i = 0; i < ids.length; i++) {
                                        ids[i] = p.hc.get(i);
                                        players.get(whom).itl.add(ids[i]);
                                    }
                                    p.hc.clear();
                                    for (int i = 0; i < ids.length; i++)
                                        anm += ids[i] + ",";
                                    anm += ":";
                                    for (int i = 0; i < ids.length; i++)
                                        anm += p.seat + ",";
                                    anm += ":";
                                    for (int i = 0; i < ids.length; i++)
                                        anm += "h,";
                                    anm += ":";
                                    for (int i = 0; i < ids.length; i++)
                                        anm += "0,";
                                    anm += ":";
                                    for (int i = 0; i < ids.length; i++)
                                        anm += whom + ",";
                                    anm += ":";
                                    for (int i = 0; i < ids.length; i++)
                                        anm += CardData.getCardColor(ids[i]) + ",";
                                    anm += ":";
                                    for (int i = 0; i < ids.length; i++)
                                        anm += CardData.getColorCount(players.get(whom).itl,
                                                                      CardData.getCardColor(ids[i]))
                                                + ",";
                                    anm += (CardData.isFalse(ids) ? ":������:-1" : "-1:-1");
                                    String del = "�R�h��P:";
                                    for (int i = 0; i < ids.length; i++)
                                        del += +ids[i] + ",";
                                    print(dier.seat, del + ":-1");
                                    print(anm);
                                    checkWinOrDead(dier.seat, whom, false, ids);
                                    listening();
                                }
                                else {
                                    String anm = "�ʦh�P�ʵe:";
                                    int[] ids = new int[3];
                                    for (int i = 0; i < 3; i++) {
                                        ids[i] = Shuffle.random(p.hc);
                                        p.hc.remove((Integer) ids[i]);
                                        players.get(whom).itl.add(ids[i]);
                                    }
                                    for (int i = 0; i < 3; i++)
                                        anm += ids[i] + ",";
                                    anm += ":";
                                    for (int i = 0; i < 3; i++)
                                        anm += p.seat + ",";
                                    anm += ":";
                                    for (int i = 0; i < 3; i++)
                                        anm += "h,";
                                    anm += ":";
                                    for (int i = 0; i < 3; i++)
                                        anm += p.hc.size() + ",";
                                    anm += ":";
                                    for (int i = 0; i < 3; i++)
                                        anm += whom + ",";
                                    anm += ":";
                                    for (int i = 0; i < 3; i++)
                                        anm += CardData.getCardColor(ids[i]) + ",";
                                    anm += ":";
                                    for (int i = 0; i < 3; i++)
                                        anm += CardData.getColorCount(players.get(whom).itl,
                                                                      CardData.getCardColor(ids[i]))
                                                + ",";
                                    anm += (CardData.isFalse(ids) ? ":������:-1" : ":-1:-1");
                                    String del = "�R�h��P:";
                                    for (int i = 0; i < ids.length; i++)
                                        del += +ids[i] + ",";
                                    print(dier.seat, del + ":-1");
                                    print(anm);
                                    checkWinOrDead(dier.seat, whom, false, ids);
                                    listening();
                                }
                            }
                        }
                        else
                            print("������r:-1");
                    }
                    else if (p.chr.equals("�B��")) {
                        for (Player $p : getAlivePlayers()) {
                            if (!$p.hc.isEmpty()) {
                                if (askUseSkill(p, 116)) {
                                    String pls = "";
                                    for (Player $$p : getAlivePlayers())
                                        if (!$$p.hc.isEmpty())
                                            pls += $$p.seat + ",";
                                    ask_th = new MessageTimer(p.seat, "��ܪ��a:-1", wt, "��ܪ��a");
                                    print("��@�쪱�a:" + pls + ":false:�A�n���֪���P�H");
                                    ask_th.start();
                                    int whom1 = Integer.parseInt(ask_th.get());
                                    print(p.seat, "����ʧ@");
                                    if (whom1 != -1) {
                                        ask_th = new MessageTimer(p.seat, "��ܱ���:-1", wt, "��ܱ���");
                                        print(p.seat,
                                              "��@�i����:" + CardData.getCards(players.get(whom1).hc) +
                                                      ":false:����A�n��������P");
                                        ask_th.start();
                                        int h = Integer.parseInt(ask_th.get());
                                        if (h != -1) {
                                            for (Player $$p : getAlivePlayers())
                                                pls += $$p.seat + ",";
                                            ask_th = new MessageTimer(p.seat, "��ܪ��a:-1", wt, "��ܪ��a");
                                            print("��@�쪱�a:" + pls + ":false:�A�n�N�o�i��P���֡H");
                                            ask_th.start();
                                            int whom2 = Integer.parseInt(ask_th.get());
                                            print("������r:-1");
                                            print(p.seat, "����ʧ@");
                                            if (whom2 != -1) {
                                                players.get(whom1).hc.remove((Integer) h);
                                                players.get(whom2).itl.add(h);
                                                print("�ޯ�ʵe:�B��:���:�B�ӫ��" +
                                                              (new Random().nextInt(2) + 1) + ":" + p +
                                                              ":"
                                                              + whom1);
                                                print(whom1, "�R�@��P:" + h + ":-1");
                                                print("�ʤ@�P�ʵe:" + h + ":" + whom1 + ":h:" +
                                                              players.get(whom1).hc.size() + ":"
                                                              + whom2 + ":" + CardData.getCardColor(h) +
                                                              ":"
                                                              + CardData.getColorCount(
                                                        players.get(whom2).itl,
                                                        CardData.getCardColor(h))
                                                              + (CardData.isTrue(h) ? ":-1:-1" :
                                                        ":������:-1"));
                                                checkWinOrDead(p.seat, whom2, false, h);
                                            }
                                        }
                                        else
                                            print("������r:-1");
                                    }
                                    else
                                        print("������r:-1");
                                }
                                break;
                            }
                        }
                    }
                }
                p = players.get(getNextPlayer(p.seat, false));
            } while (p.seat != pri);
        }

        public int drawcard() throws NoCardException {
            if (mt.isEmpty()) {
                if (trash.isEmpty())
                    throw new NoCardException("�P�w�L�P");
                else {
                    mt.addAll(Shuffle.shuffle(trash));
                    trash.clear();
                    return mt.remove(0);
                }
            }
            else
                return mt.remove(0);
        }

        public int game() {
            try {
                try {
                    gameInitialized();
                    choChr();
                    iniDealCards();
                    pri = 0;
                    stage = "start";
                    gameProcess();
                } catch (WinException e1) {
                    print("����ʧ@");
                    print("���a�ӧQ:" + e1.getMessage());
                    listening();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (Player pl : players)
                        if (pl != null)
                            if (pl.user != null)
                                pl.resetForNewGame();
                    rooms.set(id, new Room(id));
                    rooms.get(id).waitingplayers = this.waitingplayers;
                    for (int i = 0; i < 9; i++)
                        if (rooms.get(id).waitingplayers.get(i) != null)
                            if (rooms.get(id).waitingplayers.get(i).user == null)
                                rooms.get(id).waitingplayers.set(i, null);
                    rooms.get(id).name = this.name;
                    rooms.get(id).plyCount = this.plyCount;
                    rooms.get(id).chief = this.chief;
                    rooms.get(id).isBlocked = this.isBlocked;
                    lobbyPrint("�ж���T:" + rooms.get(id).id + ":" + rooms.get(id).name + ":0:" +
                                       rooms.get(id).plyCount + " / "
                                       + rooms.get(id).getMax());
                    for (Player p : rooms.get(id).waitingplayers) {
                        if (p != null) {
                            if (p.user != null) {
                                p.isReady = false;
                                p.setRoom(rooms.get(id));
                                p.print("�i�J�ж�:" + (rooms.get(id).chief == p.user.UID) + ":" +
                                                rooms.get(id).id + ":" + true);
                                p.print("sit:2");
                                for (int i = 0; i < 9; i++) // �i�D�����a�ж����H
                                    if (rooms.get(id).isBlocked.get(i))
                                        p.print("�ж����H:" + i + ":-2:false:false");
                                    else if (rooms.get(id).waitingplayers.get(i) == null)
                                        p.print("�ж����H:" + i + ":-1:false:false");
                                    else
                                        p.print("�ж����H:" + i + ":" + rooms.get(id).waitingplayers
                                                .get(i).user.username + ":"
                                                        + (rooms.get(id).chief == rooms.get(
                                                id).waitingplayers.get(i).user.UID) + ":"
                                                        + rooms.get(id).waitingplayers.get(i).isReady);
                            }
                        }
                    }
                } catch (StopGameException e) {
                    e.printStackTrace();
                    rooms.set(id, new Room(id));
                    rooms.get(id).waitingplayers = this.waitingplayers;
                    for (int i = 0; i < 9; i++)
                        if (rooms.get(id).waitingplayers.get(i) != null)
                            if (rooms.get(id).waitingplayers.get(i).user == null)
                                rooms.get(id).waitingplayers.set(i, null);
                    rooms.get(id).name = this.name;
                    rooms.get(id).plyCount = this.plyCount;
                    rooms.get(id).chief = this.chief;
                    rooms.get(id).isBlocked = this.isBlocked;
                    lobbyPrint("�ж���T:" + rooms.get(id).id + ":" + rooms.get(id).name + ":0:" +
                                       rooms.get(id).plyCount + " / "
                                       + rooms.get(id).getMax());
                    for (Player p : rooms.get(id).waitingplayers) {
                        if (p != null) {
                            if (p.user != null) {
                                p.isReady = false;
                                p.setRoom(rooms.get(id));
                                p.print("�i�J�ж�:" + (rooms.get(id).chief == p.user.UID) + ":" +
                                                rooms.get(id).id + ":" + true);
                                p.print("sit:2");
                                for (int i = 0; i < 9; i++) // �i�D�����a�ж����H
                                    if (rooms.get(id).isBlocked.get(i))
                                        p.print("�ж����H:" + i + ":-2:false:false");
                                    else if (rooms.get(id).waitingplayers.get(i) == null)
                                        p.print("�ж����H:" + i + ":-1:false:false");
                                    else
                                        p.print("�ж����H:" + i + ":" + rooms.get(id).waitingplayers
                                                .get(i).user.username + ":"
                                                        + (rooms.get(id).chief == rooms.get(
                                                id).waitingplayers.get(i).user.UID) + ":"
                                                        + rooms.get(id).waitingplayers.get(i).isReady);
                            }
                        }
                    }
                }
            } catch (NoPlayersException e) {
                e.printStackTrace();
                rooms.set(id, new Room(id));
                lobbyPrint("�ж���T:" + rooms.get(id).id + ":" + rooms.get(id).name + ":0:" +
                                   rooms.get(id).plyCount + " / "
                                   + rooms.get(id).getMax());
                return -1;
            }
            return 0;
        }

        public void gameInitialized() throws NoPlayersException {
            ArrayList<Integer> seats = Shuffle.getSeats(plyCount);
            ArrayList<Integer> idys = Shuffle.getTeams(plyCount);
            $chrs = Shuffle.getCharacterCards(plyCount);
            mt = Shuffle.getGameCards();
            ArrayList<Player> tmpplayers = new ArrayList<>();
            for (int i = 0; i < 9; i++)
                if (waitingplayers.get(i) != null)
                    tmpplayers.add(waitingplayers.get(i));
            for (int i = 0; i < plyCount; i++) {
                players.add(tmpplayers.get(seats.get(i)));
                tmpplayers.get(seats.get(i)).seat = i;
            }

            for (int i = 0; i < plyCount; i++) // ��o����
                players.get(i).idy = idys.get(i);

            for (Player pl : players)
                pl.print("�C���}�l:" + plyCount + ":" + pl.seat);
            StringBuffer names = new StringBuffer();
            for (Player pl : players)
                names.append(pl.user.username + ",");
            print("���a�m�W:" + names.toString());
            listening();
            for (Player pl : players) {
                pl.idy = idys.get(pl.seat); // �]�w����
                pl.print("�﨤��P:" + $chrs.get(pl.seat * 2) + ":" + $chrs.get(pl.seat * 2 + 1));
            }
            listening();
            Runnable choChr_run = () -> {
                try {
                    Thread.sleep(60000 + wtplus); // �w��
                    synchronized (stage) {
                        for (Player pl : players)
                            if (pl.chr == null) {
                                pl.print("�﨤����");
                                pout.println(pl.seat + "$��ܨ���:" +
                                                     $chrs.get(pl.seat * 2 + new Random().nextInt(2)));
                                pout.flush();
                            }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
            time_th = new Thread(choChr_run, "��ܨ���p�ɾ�");
            time_th.start();
        }

        public void gameProcess() throws WinException, NoPlayersException { // TODO
            testBlock();
            while (true) {
                if (plyCount == 0)
                    throw new NoPlayersException();
                switch (stage) {
                case "start":
                    System.err.println(
                            "�o�O���a" + pri + "(" + players.get(pri).chr + " ���^�X stage start");
                    print("sit:33");
                    boolean jinmin = false;
                    if (players.get(pri).chr.equals("�j���k") && !players.get(pri).isChrCov)
                        jinmin = askUseSkill(players.get(pri), 121);
                    if (jinmin) { // TODO �j���k�ޯ�n�Ҽ{��!
                        print("�ޯ�ʵe:�j���k:���:�j���k���2");
                        int h0, h1, h2;
                        try {
                            h0 = drawcard();
                            players.get(pri).hc.add(h0);
                            try {
                                h1 = drawcard();
                                players.get(pri).hc.add(h1);
                                try {
                                    h2 = drawcard();
                                    players.get(pri).hc.add(h2);
                                    print("��P�ʵe:" + pri + ":3:" + players.get(pri).hc.size());
                                    print(pri, "�W�h��P:" + h0 + "," + h1 + "," + h2 + ":-1");
                                } catch (NoCardException e) {
                                    e.printStackTrace();
                                    print("��P�ʵe:" + pri + ":2:" + players.get(pri).hc.size());
                                    print(pri, "�W�h��P:" + h0 + "," + h1 + ":-1");
                                }
                            } catch (NoCardException e) {
                                print("��P�ʵe:" + pri + ":1:" + players.get(pri).hc.size());
                                print(pri, "�W�@��P:" + h0 + ":-1");
                                e.printStackTrace();
                            }
                        } catch (NoCardException e) {
                            e.printStackTrace();
                        }
                        if (!players.get(pri).hc.isEmpty()) {
                            ask_th = new MessageTimer(pri, "��ܤ�P:-1", wt, "��ܤ�P");
                            print(pri, "��@�i��P:" + CardData.getCards(players.get(pri).hc) +
                                    ":true:��ܤ@�i��P��^�Ʈw��:" + wt);
                            printEx(pri, "������r:�е���" + players.get(pri).getName() + "�ާ@");
                            ask_th.start();
                            int h = Integer.parseInt(ask_th.get());
                            if (h == -1)
                                h = Shuffle.random(players.get(pri).hc);
                            players.get(pri).hc.remove((Integer) h);
                            mt.add(0, h);
                            print("������r:-1");
                            print(pri, "�R�@��P:" + h + ":-1");
                            print("�ʤ@�P�ʵe:0:" + pri + ":h:" + players.get(pri).hc.size() +
                                          ":1000:-1:-1:-1:-1");
                        }
                    }
                    else {
                        int h0, h1;
                        try {
                            h0 = drawcard();
                            players.get(pri).hc.add(h0);
                            try {
                                h1 = drawcard();
                                players.get(pri).hc.add(h1);
                                print("��P�ʵe:" + pri + ":2:" + players.get(pri).hc.size());
                                print(pri, "�W�h��P:" + h0 + "," + h1 + ":-1");
                            } catch (NoCardException e) {
                                print("��P�ʵe:" + pri + ":1:" + players.get(pri).hc.size());
                                print(pri, "�W�@��P:" + h0 + ":-1");
                            }
                        } catch (NoCardException e) {}
                        checkWinByHc(players.get(pri));
                    }
                    stage = "I";
                    clearStage();
                    break;

                case "I":
                    System.err.println(
                            "�o�O���a" + pri + "(" + players.get(pri).chr + " ���^�X stage I");
                    listening();
                    print("�}�B��r:�����ǻ��e���q");
                    if (startUsingCard()) {
                        queueLogic();
                        stopUsingCard();
                    }
                    // �Ҧ��H�����L
                    synchronized (stage) {
                        if (!queue.isEmpty()) {
                            // ����
                            calculate();
                            stage = "I";
                        }
                        else {
                            stage = "choitl";
                            clearStage();
                        }
                    }
                    break;

                case "choitl":
                    System.err.println(
                            "�o�O���a" + pri + "(" + players.get(pri).chr + " ���^�X stage choitl");
                    print(pri, "sit:35!");
                    printEx(pri, "sit:35");

                    if (players.get(pri).hc.isEmpty()) {
                        players.get(pri).isAlive = 3;
                        print("���a���`:" + pri + ":" + players.get(pri).chr + ":3:" +
                                      players.get(pri).idy + ":-1");
                        checkWinByIdy();
                        if (!e.wins.isEmpty()) {
                            throw e;
                        }
                        stage = "II";
                    }
                    else { // �p�G�L����P
                        boolean atSeat = true; // �L���b���ܡA�s���F�����L�@�_��
                        if (players.get(pri).status == 1) {
                            String hcms = "";
                            itl = -1;
                            for (int hcid : players.get(pri).hc)
                                hcms += hcid + ",";
                            print(pri, "������r:�ӶǱ����o");
                            print(pri, "��@�i��P:" + hcms + ":true:��ܤ@�i�n�o�X������:10000");
                            printEx(pri, "������r:����" + players.get(pri).getName() + "�o�X����");
                            ask_th = new MessageTimer(pri, "��ܤ�P:-1", 10000, "��ܤ�P", false);
                            ask_th.start();
                            itl = Integer.parseInt(ask_th.get());
                        }
                        else {
                            itl = -1;
                            atSeat = false;
                        }
                        synchronized (stage) {
                            if (itl == -1) { // �t�Φ۰����ﱡ��
                                time_th.interrupt();
                                print(pri, "����ʧ@");
                                int t = players.get(pri).hc.size();
                                int random = new Random().nextInt(t);
                                itl = players.get(pri).hc.get(random);
                                atSeat = false;
                            }
                            itlType = CardData.getIntelligenceType(itl);
                            if (itlType.equals("���F")) {
                                ask_th.mess = ("��ܪ��a:-1");
                                ask_th.check = "��ܪ��a";
                                ask_th.itr = true;
                                print(pri, "�R�@��P:" + itl + ":-1");
                                int whom = -1;
                                isItlCov = true;
                                if (atSeat && players.get(pri).status == 1) {
                                    String pms = "";
                                    for (Player pl : getAlivePlayers())//
                                        // �`���٬��۪���L���a�W��A�o�O���F�ؼ�
                                        if (pl.seat != pri)
                                            pms += pl.seat + ",";
                                    print(pri, "��@�쪱�a:" + pms + ":true:��ܪ��F�ؼЪ��a:-1");
                                    whom = Integer.parseInt(ask_th.get());
                                    print(pri, "����ʧ@");
                                    if (whom == -1)
                                        do
                                            whom = new Random().nextInt(players.size());
                                        while (whom == pri || players.get(whom).isAlive != 1);
                                }
                                else // �t�ο�ܥؼЪ��a
                                    do
                                        whom = new Random().nextInt(players.size());
                                    while (whom == pri || players.get(whom).isAlive !=
                                            1); // whom�O���F�ؼ�
                                players.get(pri).hc.remove((Integer) itl);
                                int cc = players.get(pri).hc.size();
                                String sound = "�y��/" + (CharacterData.isMale(players.get(pri)) ? "�k" :
                                        "�k") + "_���F";
                                at = whom;
                                print("������r:-1");
                                print("�o�X����:98:" + pri + ":" + whom + ":" + cc + ":" + sound);
                            }
                            else if (itlType.equals("�K�q")) {
                                ask_th.interrupt();
                                itlWay = true;
                                print(pri, "����ʧ@");
                                print("������r:-1");
                                players.get(pri).hc.remove((Integer) itl);
                                isItlCov = true;
                                int cc = players.get(pri).hc.size();
                                String sound = "�y��/" + (CharacterData.isMale(players.get(pri)) ? "�k" :
                                        "�k") + "_�K�q";
                                at = getNextPlayer(pri, true);
                                print(pri, "�R�@��P:" + itl + ":-1");
                                print("�o�X����:97:" + pri + ":" + at + ":" + cc + ":" + sound);
                            }
                            else if (itlType.equals("�奻")) {
                                ask_th.interrupt();
                                itlWay = true;
                                print(pri, "����ʧ@");
                                print("������r:-1");
                                players.get(pri).hc.remove((Integer) itl);
                                isItlCov = false;
                                int cc = players.get(pri).hc.size();
                                String sound = "�y��/" + (CharacterData.isMale(players.get(pri)) ? "�k" :
                                        "�k") + "_�奻";
                                at = getNextPlayer(pri, true);
                                print(pri, "�R�@��P:" + itl + ":-1");
                                print("�o�X����:" + itl + ":" + pri + ":" + at + ":" + cc + ":" + sound);
                            }
                            else {
                                new LogicException("���Ӧ��o��type: " + itlType).printStackTrace();
                                return;
                            }
                            stage = "pas";
                        }
                    }
                    clearStage();
                    break;

                case "pas":
                    System.err.println(
                            "�o�O���a" + pri + "(" + players.get(pri).chr + " ���^�X stage pas");
                    print("sit:36");
                    listening();
                    print("�}�B��r:�����ǻ����q�A�i�H�ϥνժ����s�B��w�M�I��");
                    if (startUsingCard()) {
                        queueLogic();
                        stopUsingCard();
                    }
                    // �Ҧ��H�����L
                    synchronized (stage) {
                        if (!queue.isEmpty()) {
                            // ����
                            calculate();
                            if (itl == -1)
                                stage = "II";
                            else
                                stage = "pas";
                        }
                        else {

                            if (players.get(at).ltd == 0 || players.get(at).ltd == 1)
                                stage = "arr";
                            else if (players.get(at).ltd == 2 || players.get(at).ltd == 3)
                                stage = "rec";
                            else
                                new LogicException(
                                        "�����T��ltd: ���a " + at + " �� ltd = " + players.get(at).ltd)
                                        .printStackTrace();
                        }
                    }
                    clearStage();
                    break;

                case "arr":
                    System.err.println(
                            "�o�O���a" + pri + "(" + players.get(pri).chr + " ���^�X stage arr");
                    print("sit:37");
                    listening();
                    print("�}�B��r:������F���q�A�i�H�ϥί}Ķ�M�h�^");
                    if (startUsingCard()) {
                        queueLogic();
                        stopUsingCard();
                    }
                    // �Ҧ��H�����L
                    synchronized (stage) {
                        if (!queue.isEmpty()) {
                            // ����
                            calculate();
                            stage = "arr";
                        }
                        else {
                            if (isBack) {
                                itlWay = !itlWay;
                                if (itlWay) // �f��
                                    at = getNextPlayer(at, true);
                                else // ����
                                    at = getPrevPlayer(at, true);
                                print("��������:" + at);
                                System.out.println("�I�sisBack");
                                stage = "pas";
                                clearStage();
                            }
                            else {
                                System.out.println("���I�sisBack");
                                stage = "rec";
                                clearStage();
                            }
                        }
                    }
                    break;

                case "rec": // �����q���K�B�z�p�G�L�������᪺�����ǻ��ʵe
                    System.err.println(
                            "�o�O���a" + pri + "(" + players.get(pri).chr + " ���^�X stage rec");
                    listening();
                    boolean rec = false;
                    if (at == pri || players.get(at).ltd == 1 || players.get(at).ltd == 3)
                        // TODO // �p�G�j���
                        rec = true;
                    else if (players.get(at).ltd == 2 || players.get(at).status != 1)
                        rec = false;
                    else { // �p�G�i���
                        System.out.println("�����������q�A�o�O���a" + pri + "���^�X");
                        print("sit:38");
                        printEx(at, "������r:����" + players.get(at).getName() + "��ܬO�_��������");
                        print(at, "������r:�O�_�����o�i�����H");
                        print(at, "�O�_����");
                        ask_th = new MessageTimer(at, "��������:false", wt, "��������");
                        ask_th.start();
                        rec = Boolean.parseBoolean(ask_th.get());
                        print("������r:-1");
                    }
                    synchronized (stage) {

                        if (rec) { // �p�G����
                            String color = CardData.getCardColor(itl);
                            String sound1 = null, sound2 = null;
                            if (color.equals("k")) {
                                sound1 = "�y��/"
                                        + (CharacterData.isMale(players.get(at)) ?
                                        "�k_������_" + (new Random().nextInt(3) + 1)
                                        : "�k_������_" + (new Random().nextInt(2) + 1));
                                sound2 = "������";
                            }
                            else // ��o�u����
                                sound1 = "�y��/"
                                        + (CharacterData.isMale(players.get(at)) ?
                                        "�k_�u����_" + (new Random().nextInt(3) + 1)
                                        : "�k_�u����_" + (new Random().nextInt(3) + 1));
                            players.get(at).itl.add((Integer) itl);
                            print("�}�B��r:" + players.get(at).getName() + "��������");
                            if (sound2 == null)
                                print("��������:" + isItlCov + ":" + itl + ":" + at + ":" + color + ":"
                                              + CardData.getColorCount(players.get(at).itl, color) +
                                              ":" + sound1);
                            else
                                print("��������:" + isItlCov + ":" + itl + ":" + at + ":" + color + ":"
                                              + CardData.getColorCount(players.get(at).itl, color) +
                                              ":" + sound1 + ":" + sound2);

                            checkWinOrDead(pri, at, true, itl);

                            stage = "II";

                        }
                        else { // �p�G����
                            Player $p = players.get(at);
                            if (itlType.equals("���F")) // ���F�����h�^�D�H
                                at = pri;
                            else { // �D���F
                                if (itlWay) // �f��
                                    at = getNextPlayer(at, true);
                                else // ����
                                    at = getPrevPlayer(at, true);
                            }
                            print("�}�B��r:" + $p.getName() + "�������ӱ����A�����ǻ���" +
                                          players.get(at).getName());
                            print("��������:" + at);
                            stage = "pas";
                        }
                    }

                    clearStage();
                    break;

                case "II":
                    System.err.println(
                            "�o�O���a" + pri + "(" + players.get(pri).chr + " ���^�X stage II");
                    print("sit:39");
                    listening();
                    print("�}�B��r:�^�X�����e���q");
                    if (startUsingCard()) {
                        queueLogic();
                        stopUsingCard();
                    }
                    // �Ҧ��H�����L
                    synchronized (stage) {
                        if (!queue.isEmpty()) {
                            // ����
                            calculate();
                            stage = "II";
                        }
                        else {
                            print("���a���A:-1");
                            stage = "start";
                            pri = getNextPlayer(pri, true);
                            clearRound();
                        }
                    }
                    break;
                }
            }

        }

        public ArrayList<Player> getActivePlayers() {
            ArrayList<Player> li = new ArrayList<>();
            for (Player p : players)
                if (p.isAlive == 1 && p.status == 1)
                    li.add(p);
            return li;
        }

        public int getAliveAndOnlinePlayerCount() {
            int c = 0;
            for (Player pl : players)
                if (pl.isAlive == 1 && pl.isOnline())
                    c++;
            return c;
        }

        public ArrayList<Player> getAliveOnlinePlayers() {
            // ��o�٦s���B���۪����a�M��
            ArrayList<Player> li = new ArrayList<>();
            for (Player pl : players)
                if (pl.isAlive == 1 && pl.isOnline())
                    li.add(pl);
            return li;

        }

        public ArrayList<Player> getAlivePlayers() {
            // ��o�٦s�������a�M��
            ArrayList<Player> li = new ArrayList<>();
            for (Player pl : players)
                if (pl.isAlive == 1)
                    li.add(pl);
            return li;
        }

        public ArrayList<Player> getAlivePlayersByPri() {
            // ��o�٦s�������a�M��
            ArrayList<Player> li = new ArrayList<>();
            Player pl = players.get(pri);
            do {
                if (pl.isAlive == 1)
                    li.add(pl);
                pl = players.get(getNextPlayer(pl.seat, false));
            } while (pl.seat != pri);
            return li;
        }

        public int getAlivePlayersCount() {
            int c = 0;
            for (Player pl : players)
                if (pl.isAlive == 1)
                    c++;
            return c;
        }

        public String getCardDark() {
            // ����k�ΨӧP�_�ѯ}�έ��ƺI�򪺮ĤO�A�ǰe�f���d�P��Ƹs
            // TODO�Ҽ{�j�ѯ}�ޯ�
            String dark = "";
            ArrayList<String> Q = getQueueRs();
            int s = Q.size();
            for (int q = s - 1; q >= 0; q--) {
                String[] ms = Q.get(q).split(":");
                if (Integer.parseInt(ms[0]) == -1)
                    dark += q + ",";
            }
            return dark;
        }

        public int getDeadPlayerCount() {
            int c = 0;
            for (Player pl : players)
                if (pl.isAlive == 2)
                    c++;
            return c;
        }

        public String getDieSound(String chr) {
            return null;
        }

        public int getMax() {
            int c = 0;
            for (boolean b : isBlocked)
                if (!b)
                    c++;
            return c;
        }

        public int getNextPlayer(int p, boolean isAlive) {
            try {
                if (p < 0 || p >= players.size())
                    throw new GameException("p�Ȥ����`�Cp = " + p);
                // �Ǧ^���ap���U�@�쪱�a�s���AisAlive��ܭn�D�O�_�����۪����a
                if (isAlive) {
                    int next = p;
                    do {
                        next = getNextPlayer(next, false);
                    } while (players.get(next).isAlive != 1);
                    return next;
                }
                else {
                    if (p == players.size() - 1)
                        return 0;
                    else
                        return p + 1;
                }
            } catch (TheMessageException e) {
                e.printStackTrace();
                return -1;
            }
        }

        public ArrayList<Player> getOnlinePlayers() {
            ArrayList<Player> li = new ArrayList<>();
            for (Player pl : players)
                if (pl.isOnline())
                    li.add(pl);
            return li;
        }

        public int getPrevPlayer(int p, boolean isAlive) {

            try {
                if (p < 0 || p >= players.size())
                    throw new GameException("p�Ȥ����`�Cp = " + p);
                if (players.get(p).isAlive != 1 && isAlive)
                    System.err.println("���a " + p + " �ä��s���A���A�n�D�L���W�@��s�����a");

                // �Ǧ^���ap���W�@�쪱�a�s���AisAlive��ܭn�D�O�_�����۪����a
                if (isAlive) {
                    int prev = p;
                    do {
                        prev = getPrevPlayer(prev, false);
                    } while (players.get(prev).isAlive != 1);
                    return prev;
                }
                else {
                    if (p == 0)
                        return players.size() - 1;
                    else
                        return p - 1;
                }
            } catch (TheMessageException e) {
                e.printStackTrace();
                return -1;
            }
        }

        public ArrayList<String> getQueueRs() {
            int size = queue.size();
            ArrayList<String> virqueue = new ArrayList<>();
            virqueue.addAll(queue);
            for (int i = size - 1; i >= 0; i--) {
                // ��:id:�d�P����:whom:which
                // -1:id(��L��)
                String[] q = virqueue.get(i).split(":");
                int p = Integer.parseInt(q[0]);
                int id = Integer.parseInt(q[1]);
                if (p == -1)
                    continue;
                if (id < 100 && id > 0) { // �d�P
                    switch (q[2]) {
                    case "�I��":
                        for (int j = 0; j < i; j++) {
                            String[] r = virqueue.get(j).split(":");
                            if (r[2].equals("�I��"))
                                virqueue.set(j, "-1:" + r[1] + ":" + r[2] + ":-1:-1");
                        }
                        break;
                    case "�ѯ}":
                        int targetId = Integer.parseInt(q[4]);
                        for (int j = 0; j < i; j++) {
                            String[] rs = virqueue.get(j).split(":");
                            if (Integer.parseInt(rs[1]) == targetId) {
                                virqueue.set(j, "-1:" + rs[1] + ":" + rs[2] + ":-1:-1");
                                break;
                            }
                        }
                        break;
                    }
                }
                else if (id < 200) {
                    switch (id) {
                    case 112: // ĵı
                        int targetId = Integer.parseInt(q[4]);
                        for (int j = 0; j < i; j++) {
                            String[] rs = virqueue.get(j).split(":");
                            if (Integer.parseInt(rs[1]) == targetId) {
                                virqueue.set(j, "-1:" + rs[1] + ":" + rs[2] + ":-1:-1");
                                break;
                            }
                        }
                        break;
                    case 117: // �I�W�I
                        for (int j = 0; j < i; j++) {
                            String[] r = virqueue.get(j).split(":");
                            if (r[2].equals("�I��"))
                                virqueue.set(j, "-1:" + r[1] + ":" + r[2] + ":-1:-1");
                        }
                        break;
                    }
                }
            }
            return virqueue;
        }

        public String getSeenId() {
            // ����k�Ǧ^�i�H�Q�ѯ}���d�PID
            // TODO�o�n��
            String ids = "";
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String[] qs = queue.get(i).split(":");
                int id = Integer.parseInt(qs[1]);
                if (id >= 0 && id < 100)
                    ids += i + ",";
            }
            return ids;
        }

        public int getStatus() {
            if (!stage.equals("waiting"))
                return 2;
            else
                return plyCount < getMax() ? 0 : 1;
        }

        public boolean hasCalled(int p, String k) {
            if ($key.equals(k))
                return $rc.contains((Integer) p);
            else
                new GameException("�˴��F���~ | ���T���T�� :" + k + " | " + $key).printStackTrace();
            return false;
        }

        public int hasChr(String chr) {
            for (Player pl : getAlivePlayers())
                if (pl.chr.equals(chr))
                    return pl.seat;
            return -1;
        }

        public int hasWinByFalseItl(Player p) {
            // �^�ǭ� 0�S�� 1Ĺ 2�� 3��

            if (p.chr.equals("�p��") && p.itl.size() > 5) {
                p.isAlive = 3;
                return 3;
            }
            if (CardData.getColorCount(p.itl, "k") > 2) {
                p.isAlive = 2;
                if (p.idy == 3) {
                    if (p.chr.equals("�Ѱ�") && CardData.getColorCount(p.hc, "r") > 2)
                        return 1;
                    if (p.chr.equals("�B��") && getDeadPlayerCount() > 1)
                        return 1;
                    if (p.chr.equals("�p��") && getDeadPlayerCount() == 1)
                        return 1;
                }
                return 2;
            }
            return 0;
        }

        public boolean hasWinByTrueItl(Player p) {
            if (p.chr.equals("�p��") && p.itl.size() > 5) {
                p.isAlive = 3;
                return false;
            }
            if (p.idy == 1 && CardData.getColorCount(p.itl, "r") > 2)
                return true;
            if (p.idy == 2 && CardData.getColorCount(p.itl, "b") > 2)
                return true;
            else {
                if (((p.chr.equals("�ֺ�����")) || (p.chr.equals("�Ѻj")) || (p.chr.equals("�����B��")))
                        && CardData.getColorCount(p.itl, "r") > 2)
                    return true;
                if (((p.chr.equals("����")) || (p.chr.equals("Ķ�q��")) || (p.chr.equals("�o�ݭ�")))
                        && CardData.getColorCount(p.itl, "b") > 2)
                    return true;
                if ((p.chr.equals("�m��")) && CardData.getColorCount(p.itl, "�u") > 4)
                    return true;
                if ((p.chr.equals("���j")) && p.itl.size() > 5)
                    return true;
            }
            return false;
        }

        public void iniDealCards() {
            for (Player pl : players)
                for (int i = 1; i <= 3; i++)
                    pl.hc.add(mt.remove(0));
            print("��P�ʵe:-1:3:3");
            for (int i = 0; i < plyCount; i++)
                print(i, "�W�h��P:" + players.get(i).hc.get(0) + "," + players.get(i).hc.get(1) + "," +
                        players.get(i).hc.get(2)
                        + ":-1");

        }

        public void listen(String k) {
            $rc = new ArrayList<>();
            $key = k;
        }

        public void listening() throws NoPlayersException {
            if (getActivePlayers().isEmpty())
                return;
            String mess = "�T���^���I" + new Random().nextInt();
            listen(mess);
            print("�^�ǰT��:" + mess, true);
            String readed = null;
            while (true)
                try {
                    while ((readed = pin.readLine()) != null) {
                        String[] _readed = readed.split("\\$");
                        int p = Integer.parseInt(_readed[0]);
                        String[] line = _readed[1].split(":");
                        if (line[0].equals("�T���^��")) {
                            if (call(p, line[1]))
                                return;
                            else
                                System.out.println(
                                        "���쪱�a " + p + "���T���A�w�g��" + $rc.size() + "�H�^��");
                        }
                    }
                } catch (IOException e) {
                    if (plyCount == 0)
                        throw new NoPlayersException();

                    e.printStackTrace();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
        }

        public int onlyOneTeam() {
            int r = 0, b = 0, g = 0;
            for (Player p : getAlivePlayers()) {
                if (p.idy == 1)
                    r = 1;
                else if (p.idy == 2)
                    b = 2;
                else
                    g += 4;
            }
            if (r + b + g == 1)
                return 1;
            if (r + b + g == 2)
                return 2;
            if (r + b + g == 4)
                return 3;
            else
                return 0;
        }

        public void print(ArrayList<String> mess) {
            print(mess, false);
        }

        // �ǰe�T�����Ҧ��H
        public void print(ArrayList<String> mess, boolean isAlive) {
            if (stage.equals("waiting")) {
                for (Player pl : waitingplayers)
                    if (pl != null) {
                        for (String ms : mess) {
                            pl.user.pw.println(ms);
                            System.out.println("�V " + pl.seat + " (" + pl.chr + ") �ǰe�T���G" + ms);
                        }
                        pl.user.pw.flush();
                    }
            }
            else {
                if (isAlive) {
                    for (Player pl : players)
                        if (pl != null)
                            if (pl.isOnline() && pl.isAlive == 1)
                                for (String ms : mess) {
                                    pl.user.pw.println(ms);
                                    System.out.println(
                                            "�V " + pl.seat + " (" + pl.chr + ") �ǰe�T���G" + ms);
                                }
                    for (Player pl : players)
                        if (pl != null)
                            if (pl.isOnline() && pl.isAlive == 1)
                                pl.user.pw.flush();
                }
                else {
                    for (Player pl : players)
                        if (pl != null)
                            if (pl.isOnline())
                                for (String ms : mess) {
                                    pl.user.pw.println(ms);
                                    System.out.println(
                                            "�V " + pl.seat + " (" + pl.chr + ") �ǰe�T���G" + ms);
                                }
                    for (Player pl : players)
                        if (pl != null)
                            if (pl.isOnline())
                                pl.user.pw.flush();
                }
            }
        }

        // �ǰe�T�����Y�H
        public void print(int p, ArrayList<String> mess) {
            if (stage.equals("waiting")) {
                Player pl = waitingplayers.get(p);
                if (pl != null) {
                    for (String ms : mess) {
                        pl.user.pw.println(ms);
                        System.out.println("�V " + pl.seat + " (" + pl.chr + ") �ǰe�T���G" + ms);
                    }
                    pl.user.pw.flush();
                }
            }
            else {
                Player pl = players.get(p);
                if (pl != null)
                    if (pl.isOnline()) {
                        for (String ms : mess) {
                            pl.user.pw.println(ms);
                            System.out.println("�V " + pl.seat + " (" + pl.chr + ") �ǰe�T���G" + ms);
                        }
                        pl.user.pw.flush();
                    }
            }
        }

        public void print(int p, String ms) {
            if (stage.equals("waiting")) {
                Player pl = waitingplayers.get(p);
                if (pl != null)
                    if (pl.isOnline()) {
                        pl.user.pw.println(ms);
                        System.out.println("�V " + pl.seat + " (" + pl.chr + ") �ǰe�T���G" + ms);
                        pl.user.pw.flush();
                    }
            }
            else {
                Player pl = players.get(p);
                if (pl != null)
                    if (pl.isOnline()) {
                        pl.user.pw.println(ms);
                        System.out.println("�V " + pl.seat + " (" + pl.chr + ") �ǰe�T���G" + ms);
                        pl.user.pw.flush();
                    }
            }
        }

        public void print(String ms) {
            print(ms, false);
        }

        public void print(String ms, boolean isAlive) {
            System.out.println("�w��print ����stage.equals(\"waiting\"): " + (stage.equals("waiting")));
            if (stage.equals("waiting")) {
                for (Player pl : waitingplayers)
                    if (pl != null) {
                        pl.user.pw.println(ms);
                        System.out.println("�V " + pl.seat + " (" + pl.chr + ") �ǰe�T���G" + ms);
                        pl.user.pw.flush();
                    }
            }
            else {
                if (isAlive) {
                    for (Player pl : players)
                        if (pl != null)
                            if (pl.isOnline() && pl.isAlive == 1) {
                                pl.user.pw.println(ms);
                                System.out.println("�V " + pl.seat + " (" + pl.chr + ") �ǰe�T���G" + ms);
                            }
                    for (Player pl : players)
                        if (pl != null)
                            if (pl.isOnline() && pl.isAlive == 1)
                                pl.user.pw.flush();
                }
                else {
                    for (Player pl : players)
                        if (pl != null)
                            if (pl.isOnline()) {
                                pl.user.pw.println(ms);
                                System.out.println("�V " + pl.seat + " (" + pl.chr + ") �ǰe�T���G" + ms);
                            }
                    for (Player pl : players)
                        if (pl != null)
                            if (pl.isOnline())
                                pl.user.pw.flush();
                }
            }
        }

        public void printEcpt(int p, ArrayList<String> mess) {
            printEcpt(p, mess, false);
        }

        // --------------------------------------------------------------------------------

        public void printEcpt(int p, ArrayList<String> mess, boolean isAlive) {
            if (stage.equals("waiting")) {
                for (int i = 0; i < plyCount; i++)
                    if (i != p) {
                        Player pl = waitingplayers.get(i);
                        if (pl != null)
                            if (pl.isOnline())
                                for (String ms : mess) {
                                    pl.user.pw.println(ms);
                                    System.out.println(
                                            "�V " + pl.seat + " (" + pl.chr + ") �ǰe�T���G" + ms);
                                }

                    }
                for (int i = 0; i < plyCount; i++)
                    if (i != p) {
                        Player pl = waitingplayers.get(i);
                        if (pl != null)
                            if (pl.isOnline())
                                pl.user.pw.flush();
                    }
            }
            else {
                if (isAlive) {
                    for (int i = 0; i < players.size(); i++)
                        if (i != p) {
                            Player pl = players.get(i);
                            if (pl != null)
                                if (pl.isOnline() && pl.isAlive == 1)
                                    for (String ms : mess) {
                                        pl.user.pw.println(ms);
                                        System.out.println(
                                                "�V " + pl.seat + " (" + pl.chr + ") �ǰe�T���G" + ms);
                                    }

                        }
                    for (int i = 0; i < players.size(); i++)
                        if (i != p) {
                            Player pl = players.get(i);
                            if (pl != null)
                                if (pl.isOnline() && pl.isAlive == 1)
                                    pl.user.pw.flush();
                        }
                }
                else {
                    for (int i = 0; i < players.size(); i++)
                        if (i != p) {
                            Player pl = players.get(i);
                            if (pl != null)
                                if (pl.isOnline())
                                    for (String ms : mess) {
                                        pl.user.pw.println(ms);
                                        System.out.println(
                                                "�V " + pl.seat + " (" + pl.chr + ") �ǰe�T���G" + ms);
                                    }

                        }
                    for (int i = 0; i < players.size(); i++)
                        if (i != p) {
                            Player pl = players.get(i);
                            if (pl != null)
                                if (pl.isOnline())
                                    pl.user.pw.flush();
                        }
                }
            }
        }

        public void printEcpt(int p, String ms, boolean isAlive) {
            if (stage.equals("waiting")) {
                for (int i = 0; i < plyCount; i++)
                    if (i != p) {
                        Player pl = waitingplayers.get(i);
                        if (pl != null)
                            if (pl.isOnline()) {
                                pl.user.pw.println(ms);
                                System.out.println("�V " + pl.seat + " (" + pl.chr + ") �ǰe�T���G" + ms);
                            }

                    }
                for (int i = 0; i < plyCount; i++)
                    if (i != p) {
                        Player pl = waitingplayers.get(i);
                        if (pl != null)
                            if (pl.isOnline())
                                pl.user.pw.flush();
                    }
            }
            else {
                if (isAlive) {
                    for (int i = 0; i < players.size(); i++)
                        if (i != p) {
                            Player pl = players.get(i);
                            if (pl != null)
                                if (pl.isOnline() && pl.isAlive == 1) {
                                    pl.user.pw.println(ms);
                                    System.out.println(
                                            "�V " + pl.seat + " (" + pl.chr + ") �ǰe�T���G" + ms);
                                }

                        }
                    for (int i = 0; i < players.size(); i++)
                        if (i != p) {
                            Player pl = players.get(i);
                            if (pl != null)
                                if (pl.isOnline() && pl.isAlive == 1)
                                    pl.user.pw.flush();
                        }
                }
                else {
                    for (int i = 0; i < players.size(); i++)
                        if (i != p) {
                            Player pl = players.get(i);
                            if (pl != null)
                                if (pl.isOnline()) {
                                    pl.user.pw.println(ms);
                                    System.out.println(
                                            "�V " + pl.seat + " (" + pl.chr + ") �ǰe�T���G" + ms);
                                }

                        }
                    for (int i = 0; i < players.size(); i++)
                        if (i != p) {
                            Player pl = players.get(i);
                            if (pl != null)
                                if (pl.isOnline())
                                    pl.user.pw.flush();
                        }
                }
            }
        }

        public void printEx(int p, String ms) {
            printEcpt(p, ms, false);
        }

        public void printHcCanUse() {
            printHcCanUse(getActivePlayers());
        }

        public void printHcCanUse(ArrayList<Player> pls) {
            ArrayList<String> hcdt = new ArrayList<>(); // �i�Ϊ��d�P
            for (Player pl : pls) {
                String hcms = "";

                if (pl.seat == pri && (stage.equals("I") || stage.equals("II") || stage.equals("pas"))) {
                    hcms += CardData.getCardsByFunc(pl.hc, "��w");
                    if (!stage.equals("pas")) {
                        hcms += CardData.getCardsByFunc(pl.hc, "�ձ�");
                        hcms += CardData.getCardsByFunc(pl.hc, "�u������");
                    }
                }

                if (stage.equals("pas")) {
                    boolean flag = false;
                    for (Player p : getAlivePlayers())
                        if (p.seat != pri && p.seat != pl.seat) {
                            // ���O�ڡA���O�L�A���N�i�H�ժ�F
                            flag = true;
                            break;
                        }
                    if (flag)
                        hcms += CardData.getCardsByFunc(pl.hc, "�ժ����s");
                    if (pl.seat != pri)
                        hcms += CardData.getCardsByFunc(pl.hc, "�I��");
                }
                if (pl.seat == at && stage.equals("arr")) {
                    hcms += CardData.getCardsByFunc(pl.hc, "�}Ķ");
                    if (pl.seat != pri && !CardData.getIntelligenceType(itl).equals("���F"))
                        hcms += CardData.getCardsByFunc(pl.hc, "�h�^");
                }
                if (canBurn()) // �i�ϥοN��
                    hcms += CardData.getCardsByFunc(pl.hc, "�N��");

                if (canSee()) // �i�ϥ��ѯ}
                    hcms += CardData.getCardsByFunc(pl.hc, "�ѯ}");
                hcdt.add(hcms);
            }
            for (int i = 0; i < pls.size(); i++) {
                print(pls.get(i).seat, "�ۥѥX�P:" + hcdt.get(i) + ":-1");
                checkOld(pls.get(i));
            }

            for (Player pl : pls) {
                int sk = -1;
                if (pl.isChrCov) {
                    if (pl.chr.equals("�ª���")) {
                        sk = 111;
                    }
                    else if (pl.chr.equals("���K�S�u")) {
                        if (queueHasCard())
                            sk = 112;
                    }
                    else if (pl.chr.equals("�B��")) {
                        sk = 115;
                    }
                    else if (pl.chr.equals("����")) {
                        boolean b = false;
                        for (Player $p : getAlivePlayers()) {
                            if ($p.seat != pl.seat && $p.itl.size() > 0) {
                                b = true;
                                break;
                            }
                        }
                        if (b)
                            sk = 109;
                    }
                    else if (pl.chr.equals("�{�F")) {
                        sk = 114;
                    }
                    else if (pl.chr.equals("�o�ݭ�")) {
                        if (stage.equals("pas") && pl.seat != pri)
                            sk = 117;
                    }
                    else if (pl.chr.equals("Ķ�q��")) {
                        if (stage.equals("pas"))
                            sk = 107;
                    }
                    else if (pl.chr.equals("縳D")) {
                        if (pl.hc.isEmpty())
                            sk = 138;
                    }
                    else if (pl.chr.equals("��d��")) {

                    }
                    else if (pl.chr.equals("�����y��")) {

                    }
                    else if (pl.chr.equals("ĳ��")) {

                    }
                }
                else {
                    if (pl.chr.equals("�Ѻj")) {
                        if (pl.isIdyCov) {
                            boolean b = false;
                            for (Player $p : getAlivePlayers()) {
                                if ($p.seat != pl.seat && !$p.isChrCov) {
                                    b = true;
                                    break;
                                }
                            }
                            if (b)
                                sk = 104;
                        }
                    }
                    else if (pl.chr.equals("���K�S�u")) {
                        boolean b = false;
                        for (Player $p : getAlivePlayers()) {
                            if ($p.seat != pl.seat && CardData.getColorCount($p.itl, "k") > 0) {
                                b = true;
                                break;
                            }
                        }
                        if (b)
                            sk = 113;
                    }
                    else if (pl.chr.equals("�j���k")) {
                        if (pl.isIdyCov)
                            sk = 122;
                    }
                    else if (pl.chr.equals("�m��")) {
                        if (stage.equals("I") || stage.equals("pas") || stage.equals("II")) {
                            if (CardData.getFuncCount(pl.hc, "��w") > 0) {
                                boolean b = false;
                                for (Player $p : getAlivePlayers()) {
                                    if ($p.seat != pl.seat && pl.seat != pri) {
                                        b = true;
                                        break;
                                    }
                                }
                                if (b)
                                    sk = 125;
                            }
                        }
                    }
                    else if (pl.chr.equals("���p�U")) {
                        if (pl.seat == pri) {
                            if (stage.equals("I") || stage.equals("pas") || stage.equals("II")) {
                                if (CardData.getColorCount(pl.hc, "k") > 0)
                                    sk = 146;
                            }
                        }
                    }
                    else if (pl.chr.equals("�M�W")) {
                        if (pl.seat == pri) {
                            if (stage.equals("I") || stage.equals("pas") || stage.equals("II")) {
                                if (CardData.getFuncCount(pl.hc, "�ձ�") > 0 || CardData.getFuncCount(
                                        pl.hc, "�h�^") > 0)
                                    sk = 130;
                            }
                        }
                    }
                    else if (pl.chr.equals("¾�~����")) {
                        if (pl.seat == pri) {
                            if (stage.equals("I") || stage.equals("pas") || stage.equals("II")) {
                                if (CardData.getFuncCount(pl.hc, "�ժ����s") > 0)
                                    sk = 135;
                            }
                        }
                    }
                    else if (pl.chr.equals("縳D")) {
                        if (stage.equals("pas")) {
                            boolean b = false;
                            System.out.println("�}�l�ˬd縳D");
                            for (Player $p : getAlivePlayers()) {
                                if ($p.seat != pri && $p.seat != pl.seat) {
                                    System.out.println("$p.seat " + $p.seat);
                                    System.out.println("pri " + pri);
                                    System.out.println("pl.seat " + pl.seat);
                                    b = true;
                                    break;
                                }
                            }
                            if (b)
                                sk = 137;
                        }
                    }
                    else if (pl.chr.equals("�ֺ�����")) {
                        if (stage.equals("pas")) {
                            if (isItlCov && itlType.equals("�K�q") && CardData.getColorCount(pl.hc,
                                                                                             "k") > 0) {
                                sk = 139;
                            }
                        }
                    }
                    else if (pl.chr.equals("�����B��")) {
                        if (stage.equals("arr") && at == pl.seat && pl.hc.size() > 0)
                            sk = 142;
                    }
                    else if (pl.chr.equals("�p��")) {
                        if (pl.hc.size() > 3) {
                            boolean b = false;
                            for (Player $p : getAlivePlayers()) {
                                if ($p.seat != pl.seat && !$p.itl.isEmpty()) {
                                    b = true;
                                    break;
                                }
                            }
                            if (b)
                                sk = 144;
                        }
                    }
                    else if (pl.chr.equals("�X���s")) {

                    }
                    else if (pl.chr.equals("�d���p�j")) {

                    }
                }
                if (sk != -1)
                    print(pl.seat, "�ޯ�]�w:" + sk + ":1");
            }
        }

        public void printHcCanUse(Player pl) {
            ArrayList<Player> pls = new ArrayList<>();
            print(pl.seat, "�{�ɭ���");
            pls.add(pl);
            printHcCanUse(pls);
        }

        public boolean queueHasCard() {
            for (String q : queue) {
                int id = Integer.parseInt(q.split(":")[1]);
                if (id < 100)
                    return true;
            }
            return false;
        }

        public void queueLogic() throws WinException, NoPlayersException {

            String readed = null;

            while (true) {
                try {
                    while ((readed = pin.readLine()) != null) {
                        int $ = readed.indexOf("$"), p = -1;
                        // $�e�O�ǰT���̦츹�A��O����
                        if ($ == -1) // �T���~���ǻ�
                            new LogicException("$ = -1").printStackTrace();
                        else
                            p = Integer.parseInt(readed.substring(0, $));
                        String[] line = readed.substring($ + 1).split(":");
                        switch (line[0]) {
                        case "�ϥΤ�P":
                        case "�ϥΧޯ�":
                            // �ϥΥd�P:���a�츹:�d�P�s��
                            synchronized (stage) {
                                if (canUseCard) {
                                    int id = Integer.parseInt(line[1]);
                                    if (id != -1 && id != 100 && id != 103 && id != 105) {
                                        queueOldSkill = new OldSkill[queue.size() + 1];
                                        for (Player $p : getAlivePlayers())
                                            $p.hasPrepareOldSkill = false;
                                    }
                                    if (id == -1) {
                                        print(p, "����ʧ@");
                                        if (call(p, "���X�P"))
                                            return;
                                    }
                                    else if (id < 100) {
                                        String type = CardData.getCardFunc(id);
                                        switch (type) {
                                        case "��w":
                                            String whom = "";
                                            players.get(p).sit = 1;
                                            players.get(p).cardId = id;
                                            for (int i = 0; i < players.size(); i++)
                                                if (p != i && pri != i)
                                                    whom += +i + ",";
                                            if (whom.equals("")) {
                                                new LogicException("�䤣��i�H�ϥΥd�P����H�C");
                                                return;
                                            }
                                            ArrayList<String> mess = new ArrayList<>();
                                            switch (stage) {
                                            case "I":
                                                mess.add("sit:341");
                                                break;
                                            case "pas":
                                                mess.add("sit:361");
                                                break;
                                            case "arr":
                                                mess.add("sit:371");
                                                break;
                                            case "II":
                                                mess.add("sit:391");
                                                break;
                                            default:
                                                new LogicException("���Ӧ��o��stage��: " + stage);
                                            }
                                            mess.add("��@�쪱�a:" + whom + ":false:��ܤ@��n��w�����a:-1");
                                            players.get(p).print(mess);
                                            break;
                                        case "�ժ����s":
                                            whom = "";
                                            players.get(p).sit = 2;
                                            players.get(p).cardId = id;
                                            for (int i = 0; i < players.size(); i++)
                                                if (p != i && pri != i)
                                                    whom += i + ",";
                                            if (whom.equals("")) {
                                                new LogicException("�䤣��i�H�ϥΥd�P����H�C");
                                                return;
                                            }
                                            mess = new ArrayList<>();
                                            switch (stage) {
                                            case "I":
                                                mess.add("sit:342");
                                                break;
                                            case "pas":
                                                mess.add("sit:362");
                                                break;
                                            case "arr":
                                                mess.add("sit:372");
                                                break;
                                            case "II":
                                                mess.add("sit:392");
                                                break;
                                            default:
                                                new LogicException("���Ӧ��o��stage��: " + stage);
                                            }
                                            mess.add("��@�쪱�a:" + whom +
                                                             ":false:��ܤ@��n�ժ����s�����a:-1");
                                            players.get(p).print(mess);
                                            break;
                                        case "�ձ�":
                                            whom = "";
                                            players.get(p).sit = 3;
                                            players.get(p).cardId = id;
                                            for (int i = 0; i < players.size(); i++)
                                                if (p != i && pri != i)
                                                    whom += i + ",";
                                            if (whom.equals("")) {
                                                new LogicException("�䤣��i�H�ϥΥd�P����H�C");
                                                return;
                                            }
                                            mess = new ArrayList<>();
                                            switch (stage) {
                                            case "I":
                                                mess.add("sit:344");
                                                break;
                                            case "pas":
                                                mess.add("sit:364");
                                                break;
                                            case "arr":
                                                mess.add("sit:374");
                                                break;
                                            case "II":
                                                mess.add("sit:394");
                                                break;
                                            default:
                                                new LogicException("���Ӧ��o��stage��: " + stage);
                                            }
                                            mess.add("��@�쪱�a:" + whom + ":false:��ܤ@��n�ձ������a:-1");
                                            players.get(p).print(mess);
                                            break;
                                        case "�N��":
                                            whom = "";
                                            for (int i = 0; i < players.size(); i++)
                                                if (CardData.getColorCount(players.get(i).itl, "k") > 0)
                                                    whom += +i + ",";
                                            if (whom.equals("")) {
                                                new LogicException("�䤣��i�H�ϥΥd�P����H�C");
                                                return;
                                            }
                                            mess = new ArrayList<>();
                                            switch (stage) {
                                            case "I":
                                                mess.add("sit:343");
                                                break;
                                            case "pas":
                                                mess.add("sit:363");
                                                break;
                                            case "arr":
                                                mess.add("sit:373");
                                                break;
                                            case "II":
                                                mess.add("sit:393");
                                                break;
                                            default:
                                                new LogicException("���Ӧ��o��stage��: " + stage);
                                            }
                                            mess.add("��@�쪱�a:" + whom + ":false:��ܤ@��n�N�쪺���a:-1");
                                            players.get(p).sit = 4;
                                            players.get(p).cardId = id;
                                            print(p, mess);
                                            break;
                                        case "�ѯ}": // TODO �٨S�˦n�o������
                                            if (queue.isEmpty()) {
                                                new LogicException("�o�ͨS���d�P�i�H�ѯ}�����~��~!")
                                                        .printStackTrace();
                                                return;
                                            }
                                            players.get(p).sit = 6;
                                            players.get(p).cardId = id;
                                            players.get(p).print("��@�i�d�P:" + getSeenId() +
                                                                         ":false:��ܭn�ѯ}���d�P:-1");
                                            break;
                                        case "�u������":
                                            stopUsingCard();
                                            players.get(p).hc.remove((Integer) id);
                                            int cc = players.get(p).hc.size();
                                            String sound = "�y��/" + (CharacterData.isMale(
                                                    players.get(p)) ? "�k_" : "�k_")
                                                    + "�u������";
                                            mess = new ArrayList<>();
                                            mess.add("�R�@��P:" + id + ":-1");
                                            mess.add("���a�ʧ@:" + p + ":" + id + ":-1:-1:" + cc + ":" +
                                                             sound);
                                            queue.add(p + ":" + id + ":�u������:-1:-1");
                                            print(p, mess);
                                            printEx(p, "���a�ʧ@:" + p + ":" + id + ":-1:-1:" + cc + ":" +
                                                    sound);
                                            listening();
                                            startUsingCard();
                                            break;
                                        case "�}Ķ":
                                            // TODO �B�z�@�U�U����

                                            stopUsingCard();
                                            players.get(p).hc.remove((Integer) id);
                                            cc = players.get(p).hc.size();
                                            sound = "�y��/" + (CharacterData.isMale(players.get(p)) ?
                                                    "�k_" : "�k_") + "�}Ķ";
                                            mess = new ArrayList<>();
                                            mess.add("�R�@��P:" + id + ":-1");
                                            mess.add("���a�ʧ@:" + p + ":" + id + ":" + p + ":300:" + cc +
                                                             ":" + sound);
                                            queue.add(p + ":" + id + ":�}Ķ:-1:-1");
                                            print(p, mess);
                                            printEx(p,
                                                    "���a�ʧ@:" + p + ":" + id + ":" + p + ":300:" + cc +
                                                            ":" + sound);
                                            listening();
                                            startUsingCard();
                                            break;
                                        case "�h�^":
                                            stopUsingCard();
                                            players.get(p).hc.remove((Integer) id);
                                            cc = players.get(p).hc.size();
                                            sound = "�y��/" + (CharacterData.isMale(players.get(p)) ?
                                                    "�k_" : "�k_") + "�h�^";
                                            mess = new ArrayList<>();
                                            mess.add("�R�@��P:" + id + ":-1");
                                            mess.add("���a�ʧ@:" + p + ":" + id + ":" + p + ":300:" + cc +
                                                             ":" + sound);
                                            queue.add(p + ":" + id + ":�h�^:-1:-1");
                                            print(p, mess);
                                            printEx(p,
                                                    "���a�ʧ@:" + p + ":" + id + ":" + p + ":300:" + cc +
                                                            ":" + sound);
                                            listening();
                                            startUsingCard();
                                            break;
                                        case "�I��":
                                            stopUsingCard();
                                            players.get(p).hc.remove((Integer) id);
                                            cc = players.get(p).hc.size();
                                            sound = "�y��/" + (CharacterData.isMale(players.get(p)) ?
                                                    "�k_" : "�k_")
                                                    + (hasTackled ? "�A�I��" : "�I��");
                                            queue.add(p + ":" + id + ":�I��:-1:-1");
                                            String dark = getCardDark();
                                            mess = new ArrayList<>();
                                            mess.add("�R�@��P:" + id + ":-1");
                                            mess.add("���a�ʧ@:" + p + ":" + id + ":" + p + ":300:" + cc +
                                                             ":" + sound);
                                            if (!dark.equals(""))
                                                mess.add("�d�P���f:" + dark);
                                            print(p, mess);
                                            printEx(p,
                                                    "���a�ʧ@:" + p + ":" + id + ":" + p + ":300:" + cc +
                                                            ":" + sound);
                                            if (!dark.equals(""))
                                                printEx(p, "�d�P���f:" + dark);
                                            hasTackled = true;
                                            listening();
                                            startUsingCard();
                                            break;
                                        default:
                                            new LogicException("���~���d�P����: " + type)
                                                    .printStackTrace();
                                            return;
                                        }
                                    }
                                    else { // �ϥΧޯ�
                                        if (id == 100) { // �Ѱ��N�p
                                            String cs = "";
                                            ArrayList<String> queueRs = getQueueRs();
                                            for (int i = 0; i < queueRs.size(); i++) {
                                                String[] q = queueRs.get(i).split(":");
                                                if ((q[2].equals("�ձ�") || q[2].equals("��w")) &&
                                                        Integer.parseInt(q[3]) == p)
                                                    cs += i + ",";
                                            }
                                            players.get(p).sit = 100;
                                            print(p, "��@�i�d�P:" + cs +
                                                    ":false:��ܤ@�i�n Ĳ�o \"�N�p\" ���d�P:-1");
                                        }
                                        else if (id == 103) { // �Ѻj�N�p
                                            String cs = "";
                                            ArrayList<String> queueRs = getQueueRs();
                                            for (int i = 0; i < queueRs.size(); i++) {
                                                String[] q = queueRs.get(i).split(":");
                                                if ((q[2].equals("�ձ�") || q[2].equals("��w")) &&
                                                        Integer.parseInt(q[3]) == p)
                                                    cs += i + ",";
                                            }
                                            players.get(p).sit = 103;
                                            print(p, "��@�i�d�P:" + cs +
                                                    ":false:��ܤ@�i�n Ĳ�o \"�N�p \"���d�P:-1");
                                        }
                                        else if (id == 105) { // ���O��
                                            String cs = "";
                                            ArrayList<String> queueRs = getQueueRs();
                                            for (int i = 0; i < queueRs.size(); i++) {
                                                String[] q = queueRs.get(i).split(":");
                                                if ((q[2].equals("�ѯ}") || Integer.parseInt(q[1]) == 112)
                                                        && Integer.parseInt(q[3]) == p)
                                                    cs += i + ",";
                                            }
                                            players.get(p).sit = 105;
                                            print(p, "��@�i�d�P:" + cs +
                                                    ":false:��ܤ@�i�n Ĳ�o \"���O�� \"���d�P:-1");
                                        }
                                        if (id == 104) { // �X��
                                            players.get(p).sit = id;
                                            String pls = "";
                                            for (Player $p : getAlivePlayers())
                                                if ($p.seat != p)
                                                    pls += $p.seat + ",";
                                            print(p, "��@�쪱�a:" + pls + ":false:��ܭn�\�񪺪��a:-1");
                                        }
                                        else if (id == 107) {// �tĶ
                                            players.get(p).isChrCov = false;
                                            queue.add(p + ":" + id + ":-1:-1:-1");
                                            stopUsingCard();
                                            print("½�}����:" + p + ":Ķ�q��");
                                            print("�ޯ�ʵe:Ķ�q��:�tĶ:Ķ�q���tĶ" +
                                                          (new Random().nextInt(2) + 1));
                                            print("���a�ʧ@:" + p + ":107:" + p + ":300:-1:-1");
                                            startUsingCard();
                                        }
                                        else if (id == 109) {// ����
                                            players.get(p).sit = id;
                                            String pls = "";
                                            for (Player $p : getAlivePlayers())
                                                if ($p.seat != p && $p.itl.size() > 0)
                                                    pls += $p.seat + ",";
                                            print(p, "��@�쪱�a:" + pls + ":false:��ܤ@�쪱�a:-1");
                                        }
                                        else if (id == 111) {// ��V
                                            players.get(p).sit = id;
                                            String pls = "";
                                            for (Player $p : getAlivePlayers())
                                                if ($p.seat != p)
                                                    pls += $p.seat + ",";
                                            print(p, "��@�쪱�a:" + pls + ":false:��ܧA�뤧�J�������a:-1");
                                        }
                                        else if (id == 112) {// ĵı
                                            players.get(p).sit = id;
                                            String cs = "";
                                            for (int q = 0; q < queue.size(); q++) {
                                                String[] qs = queue.get(q).split(":");
                                                if (Integer.parseInt(qs[1]) < 100)
                                                    cs += q + ",";
                                            }
                                            print(p, "��@�i�d�P:" + cs + ":false:��ܭn�ѯ}���d�P:-1");
                                        }
                                        else if (id == 113) {// ���@
                                            players.get(p).sit = id;
                                            String pls = "";
                                            for (Player $p : getAlivePlayers())
                                                if ($p.seat != p && CardData.getColorCount($p.itl, "k") >
                                                        0)
                                                    pls += $p.seat + ",";
                                            print(p, "��@�쪱�a:" + pls + ":false:��ܧA�n���@�����a:-1");
                                        }
                                        else if (id == 114) {// ����
                                            players.get(p).sit = id;
                                            String pls = "";
                                            for (Player $p : getAlivePlayers())
                                                if ($p.seat != p)
                                                    pls += $p.seat + ",";
                                            print(p, "��@�쪱�a:" + pls + ":false:��ܭn���������a:-1");
                                        }
                                        else if (id == 115) {// ����
                                            players.get(p).sit = id;
                                            String pls = "";
                                            for (Player $p : getAlivePlayers())
                                                if ($p.seat != p)
                                                    pls += $p.seat + ",";
                                            print(p, "��@�쪱�a:" + pls + ":false:��ܤ@�쪱�a:-1");
                                        }
                                        else if (id == 117) {// �I�W�I
                                            queue.add(p + ":" + id + ":-1:-1:-1");
                                            stopUsingCard();
                                            print("½�}����:" + p + ":�o�ݭ�");
                                            print("�ޯ�ʵe:�o�ݭ�:�I�W�I:�o�ݭ��I�W�I" +
                                                          (new Random().nextInt(2) + 1));
                                            return; // ����
                                        }
                                        else if (id == 122) {// �R��
                                            players.get(p).isIdyCov = false;
                                            queue.add(p + ":" + id + ":-1:-1:-1");
                                            stopUsingCard();
                                            print("½�}����:" + p + ":" + players.get(p).idy);
                                            print("�ޯ�ʵe:�j���k:�R��:�j���k�R��" +
                                                          (new Random().nextInt(3) + 1));
                                            print("���a�ʧ@:" + p + ":" + id + ":-1:-1:-1:-1");
                                            startUsingCard();
                                        }
                                        else if (id == 125) {// �ӾU
                                            players.get(p).sit = id;
                                            String cs = CardData.getCardsByFunc(players.get(p).hc,
                                                                                "��w");
                                            print(p, "��@�i��P:" + cs + ":false:��ܤ@�i��w:-1");
                                        }
                                        else if (id == 130) {// �G�C
                                            players.get(p).sit = id;
                                            String cs = CardData.getCardsByFunc(players.get(p).hc,
                                                                                "�ձ�");
                                            cs += CardData.getCardsByFunc(players.get(p).hc, "�h�^");
                                            print(p, "��@�i��P:" + cs + ":false:��ܤ@�i�h�^�θձ�:-1");
                                        }
                                        else if (id == 135) {// �w��
                                            players.get(p).sit = id;
                                            String cs = CardData.getCardsByFunc(players.get(p).hc,
                                                                                "�ժ����s");
                                            print(p, "��@�i��P:" + cs + ":false:��ܤ@�i�ժ����s:-1");
                                        }
                                        else if (id == 137) {//
                                            players.get(p).sit = id;
                                            String pls = "";
                                            for (Player $p : getAlivePlayers())
                                                if ($p.seat != p && $p.seat != pri)
                                                    pls += $p.seat + ",";
                                            print(p, "��@�쪱�a:" + pls + ":false:��ܤ@�쪱�a:-1");
                                        }
                                        else if (id == 138) {// �u�ĵL��
                                            stopUsingCard();
                                            players.get(p).isChrCov = false;
                                            int h0 = -1;
                                            try {
                                                h0 = drawcard();
                                                players.get(p).hc.add(h0);
                                            } catch (NoCardException e) {
                                                e.printStackTrace();
                                            }
                                            print("½�}����:" + p + ":縳D");
                                            print("�ޯ�ʵe:縳D:�u�ĵL��:縳D�u�ĵL��" +
                                                          (new Random().nextInt(2) + 1));
                                            if (h0 != -1) {
                                                print("��P�ʵe:" + p + ":1:" + players.get(p).hc.size());
                                                print(p, "�W�@��P:" + h0 + ":-1");
                                            }
                                            startUsingCard();
                                        }
                                        else if (id == 139) {// ���S
                                            players.get(p).sit = id;
                                            String cs = CardData.getCardsByColor(players.get(p).hc, "k");
                                            print(p, "��@�i��P:" + cs + ":false:��ܤ@�i�n��󪺶¦��P:-1");
                                        }
                                        else if (id == 142) {// ���
                                            players.get(p).sit = id;
                                            String cs = CardData.getCards(players.get(p).hc);
                                            print(p, "��@�i��P:" + cs + ":false:��ܤ@�i�n��󪺤�P:-1");
                                        }
                                        else if (id == 144) {// ���R
                                            players.get(p).sit = id;
                                            String pls = "";
                                            for (Player $p : getAlivePlayers()) {
                                                if (!$p.itl.isEmpty() && $p.seat != p)
                                                    pls += $p.seat + ",";
                                            }
                                            print(p, "��@�쪱�a:" + pls + ":false:��ܤ@�쪱�a:-1");

                                            // TODO�h����P
                                        }
                                        else if (id == 146) {// ����
                                            players.get(p).sit = id;
                                            String cs = CardData.getCardsByColor(players.get(p).hc, "k");
                                            print(p, "��@�i��P:" + cs + ":false:��ܤ@�i�¦��P:-1");
                                        }
                                        else if (id == 150) {// ���v

                                        }
                                        else if (id == 152) {// �w�I

                                        }
                                        else if (id == 154) {// �ƼC

                                        }
                                        else if (id == 156) {// ����

                                        }
                                        else if (id == 159) {// ����

                                        }
                                        else if (id == 107) {

                                        }
                                    }
                                }
                            }
                            break;
                        case "��ܪ��a":
                            synchronized (stage) {
                                if (canUseCard) {
                                    // ��ܪ��a:���a�츹
                                    int sit = players.get(p).sit;
                                    if (sit == 0) {
                                        new LogicException("�S�εP���H��F�ӤH��: ���a" + p)
                                                .printStackTrace();
                                        return;
                                    }
                                    else {
                                        int whom = Integer.parseInt(line[1]);
                                        if (whom == -1) {
                                            printHcCanUse(players.get(p));
                                            print(p, "������r:-1");
                                        }
                                        else
                                            switch (sit) { // Player.sit�O�b�o�̥Ψ쪺��~!
                                            case 1:
                                                int id = players.get(p).cardId;
                                                players.get(p).hc.remove((Integer) id);
                                                int cc = players.get(p).hc.size();
                                                stopUsingCard();
                                                String sound = "�y��/" + (CharacterData.isMale(
                                                        players.get(p)) ? "�k_" : "�k_")
                                                        + (hasLocked.contains(p) ||
                                                        hasBeenLocked.contains(whom) ? "�A��w"
                                                        : "��w");

                                                ArrayList<String> mess = new ArrayList<>();
                                                mess.add("�R�@��P:" + id + ":-1");
                                                mess.add("���a�ʧ@:" + p + ":" + id + ":" + p + ":" +
                                                                 whom + ":" + cc + ":"
                                                                 + sound);
                                                print(p, mess);
                                                printEx(p, "���a�ʧ@:" + p + ":" + id + ":" + p + ":" +
                                                        whom + ":" + cc + ":"
                                                        + sound);
                                                queue.add(p + ":" + id + ":��w:" + whom + ":-1");
                                                hasLocked.add(players.get(p));
                                                hasBeenLocked.add(players.get(whom));
                                                players.get(p).sit = 0;
                                                listening();
                                                startUsingCard();
                                                break;
                                            case 2:
                                                id = players.get(p).cardId;
                                                players.get(p).hc.remove((Integer) id);
                                                cc = players.get(p).hc.size();
                                                stopUsingCard();
                                                sound = "�y��/" + (CharacterData.isMale(players.get(p)) ?
                                                        "�k_" : "�k_")
                                                        + "�ժ����s";
                                                mess = new ArrayList<>();
                                                mess.add("�R�@��P:" + id + ":-1");
                                                mess.add("���a�ʧ@:" + p + ":" + id + ":" + p + ":" +
                                                                 whom + ":" + cc + ":"
                                                                 + sound);
                                                print(p, mess);
                                                printEx(p, "���a�ʧ@:" + p + ":" + id + ":" + p + ":" +
                                                        whom + ":" + cc + ":"
                                                        + sound);
                                                queue.add(p + ":" + id + ":�ժ����s:" + whom + ":-1");
                                                players.get(p).sit = 0;
                                                listening();
                                                startUsingCard();
                                                break;
                                            case 3:
                                                whom = Integer.parseInt(line[1]);
                                                if (whom == -1)
                                                    printHcCanUse(players.get(p));
                                                else {
                                                    id = players.get(p).cardId;
                                                    players.get(p).hc.remove((Integer) id);
                                                    cc = players.get(p).hc.size();
                                                    stopUsingCard();
                                                    sound = null;
                                                    if (CharacterData.isMale(players.get(p))) {
                                                        if (hasTested)
                                                            sound = "�y��/�k_�A�ձ�";
                                                        else
                                                            sound = "�y��/�k_�ձ�";
                                                    }
                                                    else
                                                        sound = "�y��/�k_�ձ�" + (new Random().nextInt(
                                                                2) + 1);
                                                    hasTested = true;

                                                    mess = new ArrayList<>();
                                                    mess.add("�R�@��P:" + id + ":-1");
                                                    mess.add("���a�ʧ@:" + p + ":99:" + p + ":" + whom +
                                                                     ":" + cc + ":" +
                                                                     sound); // �s����99���\��d
                                                    print(p, mess);
                                                    printEx(p,
                                                            "���a�ʧ@:" + p + ":99:" + p + ":" + whom +
                                                                    ":" + cc + ":" +
                                                                    sound); // �s����99���\��d
                                                    queue.add(p + ":" + id + ":�ձ�:" + whom + ":-1");
                                                    players.get(p).sit = 0;
                                                    listening();
                                                    startUsingCard();
                                                }
                                                break;
                                            case 4:
                                                players.get(p).sit = 5;
                                                players.get(p).whom = whom;
                                                String itms = CardData.getCardsByColor(
                                                        players.get(whom).itl, "k");
                                                print(p, "��@�i����:" + itms +
                                                        ":false:��ܤ@�i�n�N�쪺����:-1");
                                                break;
                                            // �H�U���ޯ�:
                                            case 104: // �X��
                                                players.get(p).isIdyCov = false;
                                                queue.add(p + ":" + sit + ":-1:" + whom + ":-1");
                                                stopUsingCard();
                                                print("½�}����:" + p + ":" + players.get(p).idy);
                                                print("�ޯ�ʵe:�Ѻj:�X��:�Ѻj�X��" +
                                                              (new Random().nextInt(2) + 1) + ":" + p +
                                                              ":"
                                                              + whom);
                                                print("���a�ʧ@:" + p + ":104:" + p + ":" + whom +
                                                              ":-1:-1");
                                                listening();
                                                startUsingCard();
                                                break;
                                            case 109: // ���å���
                                                String is = CardData.getCards(players.get(whom).itl);
                                                players.get(p).whom = whom;
                                                print(p, "��@�i����:" + is +
                                                        ":false:��ܤ@�i�n��^�P�w��������:-1");
                                                break;
                                            case 111: // ��V
                                                players.get(p).isChrCov = false;
                                                queue.add(p + ":" + players.get(p).sit + ":-1:" + whom +
                                                                  ":-1");
                                                stopUsingCard();
                                                print("½�}����:" + p + ":�ª���");
                                                print("�ޯ�ʵe:�ª���:��V����:�ª�����V����" +
                                                              (new Random().nextInt(2) + 1) + ":" + p +
                                                              ":"
                                                              + whom);
                                                print("���a�ʧ@:" + p + ":111:" + p + ":" + whom +
                                                              ":-1:-1");
                                                listening();
                                                startUsingCard();
                                                break;
                                            case 113: // ���@
                                                players.get(p).whom = whom;
                                                is = CardData.getCardsByColor(players.get(whom).itl,
                                                                              "k");
                                                print(p, "��@�i����:" + is + ":false:��ܤ@�i����:-1");
                                                break;
                                            case 114:// ����
                                                players.get(p).whom = whom;
                                                if (players.get(whom).itl.isEmpty()) {
                                                    players.get(p).isChrCov = false;
                                                    stopUsingCard();
                                                    print("½�}����:" + p + ":�{�F:" + p + ":" + whom);
                                                    print("�ޯ�ʵe:�{�F:����:�{�F����" +
                                                                  (new Random().nextInt(2) + 1) + ":" +
                                                                  p + ":"
                                                                  + whom);
                                                    startUsingCard();
                                                }
                                                else {
                                                    is = CardData.getCards(players.get(whom).itl);
                                                    print(p, "��h�i����:" + is +
                                                            ":false:0:3:-1:��ܭn�N��������");
                                                }
                                                break;
                                            case 115: // ����
                                                players.get(p).isChrCov = false;
                                                queue.add(p + ":" + players.get(p).sit + ":-1:" + whom +
                                                                  ":-1");
                                                stopUsingCard();
                                                print("½�}����:" + p + ":�B��");
                                                print("�ޯ�ʵe:�B��:����:�B�ӥ���" +
                                                              (new Random().nextInt(2) + 1) + ":" + p +
                                                              ":"
                                                              + whom);
                                                print("���a�ʧ@:" + p + ":115:" + p + ":" + whom +
                                                              ":-1:-1");
                                                listening();
                                                startUsingCard();
                                                break;
                                            case 125: // �ӾU
                                                id = players.get(p).cardId;
                                                queue.add(p + ":" + id + ":��w:" + whom + ":-1");
                                                players.get(p).hc.remove((Integer) id);
                                                sound = "�y��/" + (CharacterData.isMale(players.get(p)) ?
                                                        "�k_" : "�k_")
                                                        + (hasLocked.contains(p) ||
                                                        hasBeenLocked.contains(whom) ? "�A��w"
                                                        : "��w");
                                                hasLocked.add(players.get(p));
                                                hasBeenLocked.add(players.get(whom));
                                                stopUsingCard();
                                                print("�ޯ�ʵe:�m��:�ӾU:�m�ձӾU" +
                                                              (new Random().nextInt(2) + 1));
                                                print(p, "�R�@��P:" + id + ":-1");
                                                print("���a�ʧ@:" + p + ":" + id + ":" + p + ":" + whom +
                                                              ":"
                                                              + players.get(p).hc.size() + ":" + sound);
                                                listening();
                                                startUsingCard();
                                                break;
                                            case 130: // �G�C
                                                id = players.get(p).cardId;
                                                queue.add(p + ":" + id + ":��w:" + whom + ":-1");
                                                players.get(p).hc.remove((Integer) id);
                                                sound = "�y��/" + (CharacterData.isMale(players.get(p)) ?
                                                        "�k_" : "�k_")
                                                        + (hasLocked.contains(p) ||
                                                        hasBeenLocked.contains(whom) ? "�A��w"
                                                        : "��w");
                                                hasLocked.add(players.get(p));
                                                hasBeenLocked.add(players.get(whom));
                                                stopUsingCard();
                                                print("�ޯ�ʵe:�M�W:�G�C:�M�W�G�C" +
                                                              (new Random().nextInt(2) + 1) + ":" + p +
                                                              ":"
                                                              + whom);
                                                print(p, "�R�@��P:" + id + ":-1");
                                                print("���a�ʧ@:" + p + ":" + id + ":" + p + ":" + whom +
                                                              ":"
                                                              + players.get(p).hc.size() + ":" + sound);
                                                listening();
                                                startUsingCard();
                                                break;
                                            case 135:// �w��
                                                id = players.get(p).cardId;
                                                queue.add(p + ":" + id + ":��w:" + whom + ":-1");
                                                players.get(p).hc.remove((Integer) id);
                                                sound = "�y��/" + (CharacterData.isMale(players.get(p)) ?
                                                        "�k_" : "�k_")
                                                        + (hasLocked.contains(p) ||
                                                        hasBeenLocked.contains(whom) ? "�A��w"
                                                        : "��w");
                                                hasLocked.add(players.get(p));
                                                hasBeenLocked.add(players.get(whom));
                                                stopUsingCard();
                                                print("�ޯ�ʵe:¾�~����:�w��:¾�~����w��" +
                                                              (new Random().nextInt(2) + 1) + ":" + p +
                                                              ":"
                                                              + whom);
                                                print(p, "�R�@��P:" + id + ":-1");
                                                print("���a�ʧ@:" + p + ":" + id + ":" + p + ":" + whom +
                                                              ":"
                                                              + players.get(p).hc.size() + ":" + sound);
                                                listening();
                                                startUsingCard();
                                                break;
                                            case 137: // �n�F����
                                                boolean b = players.get(whom).ltd != 1 && players.get(
                                                        whom).ltd != 3;
                                                if (b)
                                                    players.get(whom).ltd = 2;
                                                players.get(p).isChrCov = true;
                                                stopUsingCard();
                                                print("�ޯ�ʵe:縳D:�n�F����:縳D�n�F����" +
                                                              (new Random().nextInt(2) + 1) + ":" + p +
                                                              ":"
                                                              + whom);
                                                print("�\�񨤦�:" + p + ":縳D");
                                                print("���a���A:" + whom + ":" + 2 + ":" + b);
                                                listening();
                                                startUsingCard();
                                                break;
                                            case 144: // ���R //TODO
                                                players.get(p).whom = whom;
                                                String ids = CardData.getCards(players.get(whom).itl);
                                                print("��@�i����:" + ids + ":false:��ܭn���R������");
                                                break;
                                            case 146: // ����
                                                id = players.get(p).cardId;
                                                queue.add(p + ":" + id + ":��w:" + whom + ":-1");
                                                players.get(p).hc.remove((Integer) id);
                                                sound = "�y��/" + (CharacterData.isMale(players.get(p)) ?
                                                        "�k_" : "�k_")
                                                        + (hasLocked.contains(p) ||
                                                        hasBeenLocked.contains(whom) ? "�A��w"
                                                        : "��w");
                                                hasLocked.add(players.get(p));
                                                hasBeenLocked.add(players.get(whom));
                                                stopUsingCard();
                                                print("�ޯ�ʵe:���p�U:����:���p�U����" +
                                                              (new Random().nextInt(2) + 1) + ":" + p +
                                                              ":"
                                                              + whom);
                                                print("���a�ʧ@:" + p + ":" + id + ":" + p + ":" + whom +
                                                              ":"
                                                              + players.get(p).hc.size() + ":" + sound);
                                                print(p, "�R�@��P:" + id + ":-1");
                                                listening();
                                                startUsingCard();
                                                break;
                                            default:
                                                new LogicException(
                                                        "���~�����A�G���a" + p + ", sit = " + sit)
                                                        .printStackTrace();
                                                break;
                                            }
                                    }
                                }
                            }
                            break;
                        case "��ܱ���":
                            synchronized (stage) {
                                if (canUseCard) {
                                    int sit = players.get(p).sit;
                                    if (sit == 0) {
                                        new LogicException("�S�εP���H��F�ӤH��: ���a" + p)
                                                .printStackTrace();
                                        return;
                                    }
                                    else {
                                        int which = Integer.parseInt(line[1]);
                                        if (which == -1) {
                                            printHcCanUse(players.get(p));
                                            print(p, "������r:-1");
                                        }
                                        else
                                            switch (sit) {
                                            case 5:
                                                int id = players.get(p).cardId;
                                                players.get(p).hc.remove((Integer) id);
                                                int cc = players.get(p).hc.size();
                                                stopUsingCard();

                                                String sound = "�y��/" + (CharacterData.isMale(
                                                        players.get(p)) ? "�k_" : "�k_")
                                                        + "�N��";
                                                ArrayList<String> mess = new ArrayList<>();
                                                mess.add("�R�@��P:" + id + ":-1");
                                                if (p == players.get(p).whom)
                                                    mess.add("���a�ʧ@:" + p + ":" + id + ":" +
                                                                     (queue.size() + 200) + ":" + p
                                                                     + ":" + cc + ":" + sound);
                                                else
                                                    mess.add("���a�ʧ@:" + p + ":" + id + ":" + p + ":" +
                                                                     players.get(p).whom
                                                                     + ":" + cc + ":" + sound);
                                                print(p, mess);
                                                printEx(p, "���a�ʧ@:" + p + ":" + id + ":" + p + ":" +
                                                        players.get(p).whom + ":"
                                                        + cc + ":" + sound);
                                                queue.add(p + ":" + id + ":�N��:" + players.get(p).whom +
                                                                  ":" + which);
                                                players.get(p).sit = 0;
                                                players.get(p).whom = -1;
                                                listening();
                                                startUsingCard();
                                                break;
                                            case 109:// ���å���
                                                players.get(p).isChrCov = false;
                                                queue.add(p + ":109:-1:" + players.get(p).whom + ":" +
                                                                  which);
                                                stopUsingCard();
                                                print("½�}����:" + p + ":����");
                                                print("�ޯ�ʵe:����:���å���:�������å���" +
                                                              (new Random().nextInt(2) + 1) + ":" + p +
                                                              ":"
                                                              + players.get(p).whom);
                                                print("���a�ʧ@:" + p + ":109:" + p + ":" +
                                                              players.get(p).whom + ":-1:-1");
                                                listening();
                                                startUsingCard();
                                                break;
                                            case 113: // ���@
                                                int whom = players.get(p).whom;
                                                players.get(whom).itl.remove((Integer) which);
                                                players.get(p).itl.add(which);
                                                stopUsingCard();
                                                print("�ޯ�ʵe:���K�S�u:���@:���K�S�u���@" +
                                                              (new Random().nextInt(2) + 1) + ":" + p +
                                                              ":"
                                                              + players.get(p).whom);
                                                print("�ʤ@�P�ʵe:" + which + ":" + whom + ":k:"
                                                              + CardData.getColorCount(
                                                        players.get(whom).itl, "k") + ":" + p
                                                              + ":k:" +
                                                              CardData.getColorCount(players.get(p).itl,
                                                                                     "k")
                                                              + ":������:�y��/�k_������" +
                                                              (new Random().nextInt(3) + 1));
                                                checkWinOrDead(p, p, false, which);
                                                if (players.get(p).isAlive == 1) {
                                                    players.get(p).isChrCov = true;
                                                    print("�\�񨤦�:" + p + ":���K�S�u");
                                                }
                                                listening();
                                                startUsingCard();
                                                break;
                                            case 144: // ���R
                                                print(p, "�{�ɭ���");
                                                players.get(p).which = Integer.parseInt(line[1]);
                                                String cs = CardData.getCards(players.get(p).hc);
                                                print(p,
                                                      "��h�i��P:" + cs + ":false:4:4:-1:��ܥ|�i�n��󪺤�P");
                                                break;
                                            default:
                                                new LogicException(
                                                        "���~�����A�G���a" + p + ", sit = " + sit)
                                                        .printStackTrace();
                                                break;
                                            }
                                    }
                                }
                            }
                            break;
                        case "��ܥd�P":
                            // ��ܥd�P:�d�P�Ǹ��I
                            synchronized (stage) {
                                if (canUseCard) {
                                    int sit = players.get(p).sit;
                                    if (sit == 0) {
                                        new LogicException("�S�εP���H��F�ӤH��: ���a" + p)
                                                .printStackTrace();
                                        return;
                                    }
                                    else {
                                        int targetId = Integer.parseInt(line[1]);
                                        if (targetId == -1) {
                                            printHcCanUse(players.get(p));
                                            print(p, "������r:-1");
                                        }
                                        else
                                            switch (players.get(p).sit) {
                                            case 6:
                                                int whom = Integer.parseInt(
                                                        queue.get(targetId).split(":")[0]);
                                                targetId = Integer.parseInt(queue.get(targetId)
                                                                                    .split(":")[1]); // �ର�Q�ѯ}���d�P�s��
                                                int id = players.get(p).cardId;
                                                players.get(p).hc.remove((Integer) id);
                                                int cc = players.get(p).hc.size();
                                                stopUsingCard();

                                                int index = -1;
                                                for (String qs : queue) {
                                                    String q[] = qs.split(":");
                                                    if (Integer.parseInt(q[1]) == targetId) {
                                                        index = queue.indexOf(qs);
                                                        break;
                                                    }
                                                }
                                                if (index == -1)
                                                    new LogicException("���~��index: " + index)
                                                            .printStackTrace();

                                                String sound = "�y��/" + (CharacterData.isMale(
                                                        players.get(p)) ? "�k_" : "�k_")
                                                        + (hasSeen ? "�A�ѯ}" : "�ѯ}");
                                                hasSeen = true;
                                                ArrayList<String> mess = new ArrayList<>();
                                                queue.add(
                                                        p + ":" + id + ":�ѯ}:" + whom + ":" + targetId);
                                                String dark = getCardDark();
                                                mess.add("�R�@��P:" + id + ":-1");
                                                mess.add("���a�ʧ@:" + p + ":" + id + ":" + p + ":" +
                                                                 (index + 200) + ":" + cc
                                                                 + ":" + sound); // �s���[200��ܥ\��P
                                                mess.add("�d�P���f:" + dark);
                                                print(p, mess);
                                                printEx(p, "���a�ʧ@:" + p + ":" + id + ":" + p + ":" +
                                                        (index + 200) + ":" + cc
                                                        + ":" + sound); // �s���[200��ܥ\��P
                                                printEx(p, "�d�P���f:" + dark);
                                                listening();
                                                startUsingCard();
                                                break;
                                            case 100: // �Ѱ��N�p
                                            case 103:// �Ѻj�N�p
                                            case 105:// ���O��
                                                targetId = Integer.parseInt(
                                                        queue.get(targetId).split(":")[1]);
                                                index = -1;
                                                for (String qs : queue) {
                                                    String q[] = qs.split(":");
                                                    if (Integer.parseInt(q[1]) == targetId) {
                                                        index = queue.indexOf(qs);
                                                        break;
                                                    }
                                                }
                                                if (index == -1)
                                                    new LogicException("���~��index: " + index)
                                                            .printStackTrace();
                                                players.get(p).hasPrepareOldSkill = true;
                                                queueOldSkill[index] = new OldSkill(p,
                                                                                    players.get(p).sit);
                                                print(p, "�ޯ�]�w:" + sit + ":3");
                                                print(p, "������r:-1");
                                                printHcCanUse(players.get(p));
                                                break;
                                            case 112: // �j�ѯ}
                                                hasSeen = true;
                                                whom = Integer.parseInt(
                                                        queue.get(targetId).split(":")[0]);
                                                targetId = Integer.parseInt(queue.get(targetId)
                                                                                    .split(":")[1]); // �ର�Q�ѯ}���d�P�s��
                                                stopUsingCard();
                                                players.get(p).isChrCov = false;
                                                index = -1;
                                                for (String qs : queue) {
                                                    String q[] = qs.split(":");
                                                    if (Integer.parseInt(q[1]) == targetId) {
                                                        index = queue.indexOf(qs);
                                                        break;
                                                    }
                                                }
                                                if (index == -1)
                                                    new LogicException("���~��index: " + index)
                                                            .printStackTrace();
                                                queue.add(p + ":112:-1:" + whom + ":" + targetId);
                                                print("½�}����:" + p + ":���K�S�u");
                                                print("�ޯ�ʵe:���K�S�u:ĵı:���K�S�uĵı" +
                                                              (new Random().nextInt(2) + 1) + ":" + p +
                                                              ":"
                                                              + (index + 200));
                                                print("���a�ʧ@:" + p + ":112:" + p + ":" +
                                                              (index + 200) +
                                                              ":-1:-1"); // �s���[200��ܥ\��P
                                                print("�d�P���f:" + getCardDark());
                                                listening();
                                                startUsingCard();
                                                break;
                                            default:
                                                new LogicException(
                                                        "���~�����A�G���a" + p + ", sit = " + sit)
                                                        .printStackTrace();
                                                break;
                                            }
                                    }
                                }
                            }
                            break;
                        case "��ܤ�P": // TODO�m�դM�W����U�l���q�B��
                            synchronized (stage) {
                                if (canUseCard) {
                                    int sit = players.get(p).sit;
                                    if (sit == 0) {
                                        new LogicException("�S�εP���H��F�ӤH��: ���a" + p)
                                                .printStackTrace();
                                        return;
                                    }
                                    else {
                                        int id = Integer.parseInt(line[1]);
                                        if (id == -1)
                                            printHcCanUse(players.get(p));
                                        else
                                            switch (players.get(p).sit) {
                                            case 125: // �ӾU
                                            case 130: // �G�C
                                            case 135: // �w��
                                            case 146:// ����
                                                players.get(p).cardId = id;
                                                String ps = "";
                                                for (Player $p : getAlivePlayers())
                                                    if ($p.seat != pri && $p.seat != p)
                                                        ps += $p.seat + ",";
                                                print(p, "��@�쪱�a:" + ps + ":false:��ܤ@��n��w�����a:-1");
                                                break;
                                            case 139: // ���S
                                                players.get(p).hc.remove((Integer) id);
                                                trash.add(id);
                                                stopUsingCard();
                                                queue.add(p + ":139:-1:-1:-1");
                                                print("�ޯ�ʵe:�ֺ�����:���S:�ֺ��������S" +
                                                              (new Random().nextInt(2) + 1));
                                                print(p, "�R�@��P:" + id + ":-1");
                                                print("��@�P�ʵe:" + id + ":" + p + ":h:" + players.get(
                                                        p).hc.size());
                                                print("���a�ʧ@:" + p + ":139:" + p + ":300:-1:-1");
                                                listening();
                                                startUsingCard();
                                                break;
                                            case 142: // ���s��� TODO:
                                                players.get(p).hc.remove((Integer) id);
                                                trash.add(id);
                                                int type0 = -1, type1 = -1;
                                                if (!isItlCov) {
                                                    type0 = itl;
                                                }
                                                else if (itlType.equals("�K�q")) {
                                                    type0 = 97;
                                                }
                                                else if (itlType.equals("���F")) {
                                                    type0 = 98;
                                                    type0 = itl;
                                                }
                                                if (itlType.equals("�K�q")) {
                                                    type1 = 97;
                                                }
                                                else if (itlType.equals("���F")) {
                                                    type1 = 98;
                                                }
                                                else if (itlType.equals("�奻")) {
                                                    type1 = 97;
                                                    itlType = "�K�q";
                                                }

                                                isItlCov = true;
                                                stopUsingCard();
                                                print("�ޯ�ʵe:�����B��:���s���:�����B�����s���" +
                                                              (new Random().nextInt(2) + 1));
                                                print(p, "�R�@��P:" + id + ":-1");
                                                print("��@�P�ʵe:" + id + ":" + p + ":h:" + players.get(
                                                        p).hc.size());
                                                print("�ʦh�P�ʵe:" + type0 + "," + type1
                                                              +
                                                              ":300,1000:-1,-1:-1,-1:1000,300:-1,-1:-1,-1:-1:-1");
                                                int tmp = itl;
                                                itl = mt.get(0);
                                                mt.set(0, tmp);
                                                listening();
                                                startUsingCard();
                                                break;
                                            default:
                                                new LogicException(
                                                        "���~�����A�G���a" + p + ", sit = " + sit)
                                                        .printStackTrace();
                                                break;
                                            }
                                    }
                                }
                            }
                            break;
                        case "��ܦh�i����":
                            synchronized (stage) {
                                if (canUseCard) {
                                    int sit = players.get(p).sit;
                                    if (sit == 0) {
                                        new LogicException("�S�εP���H��F�h�i����: ���a" + p)
                                                .printStackTrace();
                                        return;
                                    }
                                    else {
                                        if (line[1].equals("-1"))
                                            printHcCanUse(players.get(p));
                                        else
                                            switch (players.get(p).sit) {
                                            case 114: // ����
                                                int whom = players.get(p).whom;
                                                players.get(p).isChrCov = false;
                                                stopUsingCard();
                                                String[] sids = CardData.getCardsInColorOrder(line[1])
                                                        .split(",");
                                                int[] ids = new int[sids.length];
                                                for (int i = 0; i < ids.length; i++) {
                                                    ids[i] = Integer.parseInt(sids[i]);
                                                    players.get(whom).itl.remove((Integer) ids[i]);
                                                    trash.add(ids[i]);
                                                }
                                                String anms = "�ʦh�P�ʵe:";
                                                for (int i = 0; i < ids.length; i++)
                                                    anms += ids[i] + ",";
                                                anms += ":";
                                                for (int i = 0; i < ids.length; i++)
                                                    anms += whom + ",";
                                                anms += ":";
                                                for (int i = 0; i < ids.length; i++)
                                                    anms += CardData.getCardColor(ids[i]) + ",";
                                                anms += ":";
                                                for (int i = 0; i < ids.length; i++)
                                                    anms += CardData.getColorCount(players.get(whom).itl,
                                                                                   CardData.getCardColor(
                                                                                           ids[i])) +
                                                            ",";
                                                anms += ":";
                                                for (int i = 0; i < ids.length; i++)
                                                    anms += "999,";
                                                anms += ":";
                                                for (int i = 0; i < ids.length; i++)
                                                    anms += "-1,";
                                                anms += ":";
                                                for (int i = 0; i < ids.length; i++)
                                                    anms += "-1,";
                                                anms += ":-1:-1";
                                                print("½�}����:" + p + ":�{�F:" + p + ":" + whom);
                                                print("�ޯ�ʵe:�{�F:����:�{�F����" +
                                                              (new Random().nextInt(2) + 1) + ":" + p +
                                                              ":"
                                                              + whom);
                                                print(anms);
                                                startUsingCard();
                                                break;
                                            default:
                                                new LogicException(
                                                        "���~�����A�G���a" + p + ", sit = " + sit)
                                                        .printStackTrace();
                                                break;
                                            }
                                    }
                                }
                            }
                            break;
                        case "��ܦh�i��P":
                            synchronized (stage) {
                                if (canUseCard) {
                                    int sit = players.get(p).sit;
                                    if (sit == 0) {
                                        new LogicException("�S�εP���H��F�h�i����: ���a" + p)
                                                .printStackTrace();
                                        return;
                                    }
                                    else {
                                        if (line[1].equals("-1"))
                                            printHcCanUse(players.get(p));
                                        else
                                            switch (players.get(p).sit) {
                                            case 144: // ���R
                                                int whom = players.get(p).whom;
                                                stopUsingCard();
                                                queue.add(p + ":144:-1:" + whom + ":" + line[1]);
                                                String[] cids = (line[1]).split(",");
                                                int[] ids = new int[4];
                                                for (int i = 0; i < 4; i++) {
                                                    ids[i] = Integer.parseInt(cids[i]);
                                                    players.get(p).hc.remove((Integer) ids[i]);
                                                    players.get(whom).hc.add(ids[i]);
                                                }
                                                String anms = "�ʦh�P�ʵe:";
                                                for (int i = 0; i < ids.length; i++)
                                                    anms += ids[i] + ",";
                                                anms += ":";
                                                for (int i = 0; i < ids.length; i++)
                                                    anms += p + ",";
                                                anms += ":";
                                                for (int i = 0; i < ids.length; i++)
                                                    anms += "h,";
                                                anms += ":";
                                                for (int i = 0; i < ids.length; i++)
                                                    anms += players.get(p).hc.size() + ",";
                                                anms += ":";
                                                for (int i = 0; i < ids.length; i++)
                                                    anms += whom + ",";
                                                anms += ":";
                                                for (int i = 0; i < ids.length; i++)
                                                    anms += "h,";
                                                anms += ":";
                                                for (int i = 0; i < ids.length; i++)
                                                    anms += players.get(whom).hc.size() + ",";
                                                anms += ":-1:-1";
                                                print(p,
                                                      "�R�h��P:" + ids[0] + "," + ids[1] + "," + ids[2] +
                                                              "," + ids[3]
                                                              + ":-1");
                                                print(anms);
                                                print(whom,
                                                      "�W�h��P:" + ids[0] + "," + ids[1] + "," + ids[2] +
                                                              "," + ids[3]
                                                              + ":-1");
                                                print("�ޯ�ʵe:�p��:���R:�p�զ��R" +
                                                              (new Random().nextInt(2) + 1) + ":" + p +
                                                              ":"
                                                              + whom);
                                                print("���a�ʧ@:" + p + ":144:" + p + ":" + whom +
                                                              ":-1:-1");
                                                checkWinByHc(players.get(whom));
                                                listening();
                                                startUsingCard();
                                                break;
                                            default:
                                                new LogicException(
                                                        "���~�����A�G���a" + p + ", sit = " + sit)
                                                        .printStackTrace();
                                                break;
                                            }
                                    }
                                }
                            }
                            break;
                        default:
                            new LogicException("���~���T��: " + readed).printStackTrace();
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (e.getMessage().equals("Pipe broken") || e.getMessage().equals("Write end dead"))
                        if (plyCount == 0)
                            throw new NoPlayersException();
                    try {
                        Thread.sleep(1000);
                        continue;
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }

        public void skillWhenGetItl(int id, int sender, int getter, boolean pas)
                throws WinException, NoPlayersException {
            int[] ids = {id};
            skillWhenGetItl(ids, sender, getter, pas);
        }

        public void skillWhenGetItl(int[] id, int sender, int getter, boolean pas)
                throws WinException, NoPlayersException {
            for (Player p : getAlivePlayersByPri()) {
                System.out.println("skillWhenGetItl: " + p.chr);
                if (pas)
                    skillWhenGetPassingItl(id, sender, getter, p);
                if (!p.isChrCov && p.isAlive == 1) {
                    if (p.chr.equals("§�A�X���H")) {
                        if (CardData.isFalse(id) && players.get(getter).seat == p.seat) {
                            if (askUseSkill(p, 128)) {
                                print(p.seat, "����ʧ@");
                                print("������r:-1");
                                print("�ޯ�ʵe:§�A�X���H:�^��:§�A�X���H�^��" +
                                              (new Random().nextInt(3) + 1));
                                try {
                                    int h0 = drawcard();
                                    p.hc.add(h0);
                                    try {
                                        int h1 = drawcard();
                                        p.hc.add(h1);
                                        print("��P�ʵe:" + p.seat + ":2:" + p.hc.size());
                                        print(p.seat, "�W�h��P:" + h0 + "," + h1 + ":-1");
                                    } catch (NoCardException e) {
                                        e.printStackTrace();
                                        print("��P�ʵe:" + p.seat + ":1:" + p.hc.size());
                                        print(p.seat, "�W�@��P:" + h0 + ":-1");
                                    }
                                } catch (NoCardException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    else if (p.chr.equals("�p����")) {
                        if (CardData.isFalse(id) && players.get(getter).seat == p.seat) {
                            if (askUseSkill(p, 132)) {
                                print(p.seat, "����ʧ@");
                                print("������r:-1");
                                print("�ޯ�ʵe:�p����:�^������:�p�����^������" +
                                              (new Random().nextInt(2) + 1));
                                try {
                                    int h0 = drawcard();
                                    p.hc.add(h0);
                                    print("��P�ʵe:" + p.seat + ":1:" + p.hc.size());
                                    print(p.seat, "�W�@��P:" + h0 + ":-1");
                                } catch (NoCardException e) {
                                    e.printStackTrace();
                                }
                                if (!p.hc.isEmpty()) {
                                    ask_th = new MessageTimer(p.seat, "��ܤ�P:-1", wt, "��ܤ�P");
                                    print(p.seat, "��@�i��P:" + CardData.getCardsByColor(p.hc, "k") +
                                            ":false:��ܤ@�i��P");
                                    printEx(p.seat, "������r:�е���" + p.getName() + "�ާ@");
                                    ask_th.start();
                                    int h = Integer.parseInt(ask_th.get());
                                    print(p.seat, "����ʧ@");
                                    print("������r:-1");
                                    if (h != -1) {
                                        print(p.seat, "����ʧ@");
                                        print("������r:-1");
                                        String pls = "";
                                        for (Player $p : getAlivePlayers())
                                            pls += $p.seat + ",";
                                        ask_th = new MessageTimer(p.seat, "��ܪ��a:-1", wt, "��ܪ��a");
                                        print(p.seat, "��@�쪱�a:" + pls + ":false:��ܭn���_�����a");
                                        ask_th.start();
                                        int pl = Integer.parseInt(ask_th.get());
                                        if (pl != -1) {
                                            print(p.seat, "����ʧ@");
                                            print("������r:-1");
                                            p.hc.remove((Integer) h);
                                            players.get(pl).itl.add(h);
                                            print(p.seat, "�R�@��P:" + h + ":-1");
                                            print("�ʤ@�P�ʵe:" + h + ":" + p.seat + ":h:" + p.hc.size() +
                                                          ":" + pl + ":k:"
                                                          + CardData.getColorCount(players.get(pl).itl,
                                                                                   "k") + ":������:-1");
                                            checkWinOrDead(p.seat, pl, false, h);
                                        }
                                    }
                                }
                            }
                            else {
                                print(p.seat, "����ʧ@");
                                print("������r:-1");
                            }
                        }
                    }
                    else if (p.chr.equals("����")) {
                        if (CardData.isTrue(id) && p.seat == players.get(getter).seat) {
                            if (askUseSkill(p, 134)) {
                                print(p.seat, "����ʧ@");
                                print("������r:-1");
                                print("�ޯ�ʵe:����:�B�w�c��:�����B�w�c��" +
                                              (new Random().nextInt(2) + 1));
                                try {
                                    int h = drawcard();
                                    p.hc.add(h);
                                    print("��P�ʵe:" + p.seat + ":1:" + p.hc.size());
                                    print(p.seat, "�W�@��P:" + h + ":-1");
                                } catch (NoCardException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else {
                            print(p.seat, "����ʧ@");
                            print("������r:-1");
                        }
                    }
                    else if (p.chr.equals("�Ѫ�")) {
                        if (players.get(getter).seat == p.seat && CardData.isFalse(id) &&
                                !p.hc.isEmpty()) {
                            if (askUseSkill(p, 106)) {
                                print(p.seat, "����ʧ@");
                                print(p.seat, "��@�i��P:" + CardData.getCardsByColor(p.hc, "k") +
                                        ":false:��ܤ@�i�n���ۤv��m��������");
                                ask_th = new MessageTimer(p.seat, "��ܤ�P:-1", wt, "��ܤ�P");
                                ask_th.start();
                                int h = Integer.parseInt(ask_th.get());
                                if (h != -1) {
                                    print(p.seat, "����ʧ@");
                                    print("������r:-1");
                                    p.hc.remove((Integer) h);
                                    p.itl.add(h);
                                    p.isChrCov = true;
                                    print(p.seat, "�R�@��P:" + h + ":-1");
                                    print("�ʤ@�P�ʵe:" + h + ":" + p.seat + ":h:" + p.hc.size() + ":" +
                                                  p.seat + ":k:"
                                                  + CardData.getColorCount(p.itl, "k") + ":������:-1");
                                    print("�ޯ�ʵe:�Ѫ�:�y�����~:�Ѫ��y�����~" +
                                                  (new Random().nextInt(2) + 1));
                                    print("�\�񨤦�:" + p.seat + ":�Ѫ�");
                                    listening();
                                    checkWinOrDead(p.seat, p.seat, false, h);
                                    if (p.isAlive == 1 && !p.hc.isEmpty()) {
                                        print(p.seat, "����ʧ@");
                                        wait(p);
                                        int[][] cards = new int[getAlivePlayersCount()][2];
                                        for (int i = 0; i < cards.length; i++) {
                                            cards[i][0] = -1;
                                            cards[i][1] = -1;
                                        }
                                        for (int i = 0; i < cards.length; i++) {

                                            String pls = "";
                                            con:
                                            for (Player $p : getAlivePlayers()) {
                                                for (int $i = 0; $i < cards.length; $i++) {
                                                    if (cards[$i][0] == $p.seat)
                                                        continue con;
                                                    else if (cards[$i][0] == -1)
                                                        break;
                                                }
                                                pls += $p.seat + ",";
                                            }
                                            print(p.seat,
                                                  "��@�쪱�a:" + pls + ":false:��ܲ�" +
                                                          StringFormatter.numCh(i + 1) + "�쪱�a");
                                            ask_th = new MessageTimer(p.seat, "��ܪ��a:-1", wt, "��ܪ��a");
                                            ask_th.start();
                                            cards[i][0] = Integer.parseInt(ask_th.get());
                                            if (cards[i][0] != -1) {
                                                print(p.seat, "����ʧ@");
                                                print(p.seat, "��@�i��P:" + CardData.getCards(p.hc) +
                                                        ":false:��ܭn��m����P");
                                                ask_th = new MessageTimer(p.seat, "��ܤ�P:-1", wt,
                                                                          "��ܤ�P");
                                                ask_th.start();
                                                cards[i][1] = Integer.parseInt(ask_th.get());
                                                if (cards[i][1] != -1) {
                                                    p.hc.remove((Integer) cards[i][1]);
                                                    players.get(cards[i][0]).itl.add(cards[i][1]);
                                                    print(p.seat, "����ʧ@");
                                                    print("������r:-1");
                                                    print(p.seat, "�R�@��P:" + cards[i][1] + ":-1");
                                                    String col = CardData.getCardColor(cards[i][1]);
                                                    print("�ʤ@�P�ʵe:" + cards[i][1] + ":" + p.seat +
                                                                  ":h:" + p.hc.size() + ":"
                                                                  + cards[i][0] + ":" + col + ":"
                                                                  + CardData.getColorCount(
                                                            players.get(cards[i][0]).itl, col) + ":"
                                                                  + (col.equals("k") ? "������:-1" :
                                                            "-1:-1"));

                                                    if (hasWinByFalseItl(p) == 2)
                                                        break;

                                                    if (hasWinByTrueItl(players.get(cards[i][0]))) {
                                                        e.set(new Win(players.get(cards[i][0]), true));
                                                        checkSnakeWin();
                                                        checkWinByIdy();
                                                        checkWinTogether();
                                                        throw e;
                                                    }
                                                    else if (hasWinByFalseItl(
                                                            players.get(cards[i][0])) == 1) {
                                                        for (int j = 0; j < cards.length; j++) {
                                                            if (cards[j][0] == -1 || cards[j][1] == -1)
                                                                break;
                                                            else if (hasWinByFalseItl(
                                                                    players.get(cards[j][0])) == 2)
                                                                print("���a���`:" + cards[j][0] + ":"
                                                                              + players.get(
                                                                        cards[j][0]).chr + ":2:"
                                                                              + players.get(
                                                                        cards[j][0]).idy);
                                                        }
                                                        e.set(new Win(players.get(cards[i][0]), true));
                                                        checkWinByIdy();
                                                        checkWinTogether();
                                                        throw e;
                                                    }
                                                    else if (hasWinByFalseItl(
                                                            players.get(cards[i][0])) == 2) {
                                                        checkWinByIdy();
                                                        checkWinTogether();
                                                        if (!e.wins.isEmpty()) {
                                                            for (int j = 0; j < cards.length; j++) {
                                                                if (cards[j][0] == -1 ||
                                                                        cards[j][1] == -1)
                                                                    break;
                                                                else if (hasWinByFalseItl(
                                                                        players.get(cards[j][0])) == 2)
                                                                    print("���a���`:" + cards[j][0] + ":"
                                                                                  + players.get(
                                                                            cards[j][0]).chr + ":2:"
                                                                                  + players.get(
                                                                            cards[j][0]).idy);
                                                            }
                                                            throw e;
                                                        }
                                                    }
                                                    if (p.hc.isEmpty())
                                                        break;

                                                    else {
                                                        int ps = 0;
                                                        for (int $p = 0; $p < cards.length; $p++) {
                                                            if (cards[$p][0] == -1)
                                                                break;
                                                            ps++;
                                                        }
                                                        if (ps >= getAlivePlayersCount())
                                                            break;
                                                    }
                                                    ask_th = new MessageTimer(p.seat, "����~��:false",
                                                                              wt, "����~��");
                                                    print(p.seat,
                                                          "�O�_�~��:�O�_�n�n�~���ܲ�" +
                                                                  StringFormatter.numCh(i + 2) +
                                                                  "�쪱�a�H:5000");
                                                    ask_th.start();
                                                    if (Boolean.parseBoolean(ask_th.get()))
                                                        print(p.seat, "����ʧ@");
                                                    else
                                                        break;
                                                }
                                                else
                                                    break;
                                            }
                                            else {
                                                break;
                                            }
                                        }
                                        print(p.seat, "����ʧ@");
                                        print("������r:-1");
                                        for (int i = 0; i < cards.length; i++) {
                                            if (cards[i][0] == -1 || cards[i][1] == -1)
                                                break;
                                            else if (hasWinByFalseItl(players.get(cards[i][0])) == 2) {
                                                deadSkill(players.get(cards[i][0]));// b.���`�ޯ�;
                                            }
                                            else
                                                skillWhenGetItl(cards[i][1], p.seat, cards[i][0], false);
                                        }

                                    }
                                    else {
                                        print(p.seat, "����ʧ@");
                                        checkWinOrDead(p.seat, p.seat, false, h);
                                    }
                                }
                                else
                                    print(p.seat, "����ʧ@");
                            }
                        }
                    }
                    else if (p.chr.equals("�o�ݭ�")) {
                        if (sender == p.seat && CardData.isTrue(id)
                                && CardData.getColorCount(players.get(getter).itl, "k") > 0) {
                            if (askUseSkill(p, 118)) {
                                print(p.seat, "����ʧ@");
                                ask_th = new MessageTimer(p.seat, "��ܱ���:-1", wt, "��ܱ���");
                                print(p.seat, "��@�i����:" +
                                        CardData.getCardsByColor(players.get(getter).itl, "k")
                                        + ":false:��ܤ@�i�n�N����������");
                                printEx(p.seat, "������r:�е���" + p.getName() + "�ާ@");
                                ask_th.start();
                                int b = Integer.parseInt(ask_th.get());
                                if (b != -1) {
                                    print(p.seat, "����ʧ@");
                                    print("������r:-1");
                                    players.get(getter).itl.remove((Integer) b);
                                    print("�ޯ�ʵe:�o�ݭ�:�p���p:�o�ݭ��p���p" +
                                                  (new Random().nextInt(2) + 1) + ":" + p.seat + ":"
                                                  + getter);
                                    print("�ʤ@�P�ʵe:" + b + ":" + players.get(getter).seat + ":k:"
                                                  +
                                                  CardData.getColorCount(players.get(getter).itl, "k") +
                                                  ":999:-1:-1:-1:-1");
                                }
                            }
                            else {
                                print(p.seat, "����ʧ@");
                                print("������r:-1");
                            }
                        }
                    }
                }
            }
        }

        public void skillWhenGetPassingItl(int[] id, int sender, int getter, Player p)
                throws WinException, NoPlayersException {
            if (!p.isChrCov) {
                if (p.chr.equals("�ǵs�E�E")) {
                    if (CardData.isFalse(id)) {
                        boolean use = false;
                        int skillId = -1;
                        if (p.seat == getter) {
                            use = askUseSkill(p, 149);
                            skillId = 149;
                        }
                        else if (p.seat == sender) {
                            use = askUseSkill(p, 148);
                            skillId = 148;
                        }
                        if (use) {
                            print(p.seat, "����ʧ@");
                            String pls = "";
                            for (Player $$p : getAlivePlayers())
                                if ($$p.seat != p.seat && !$$p.hc.isEmpty())
                                    pls += $$p.seat + ",";
                            ask_th = new MessageTimer(pri, "��ܪ��a:-1", wt, "��ܪ��a");
                            print(p.seat, "��@�쪱�a:" + pls + ":false:��ܤ@�쪱�a");
                            printEx(p.seat, "������r:�е���" + p.getName() + "�ާ@");
                            ask_th.start();
                            int whom = Integer.parseInt(ask_th.get());
                            if (whom != -1) {
                                print(p.seat, "����ʧ@");
                                print("������r:-1");
                                int h = Shuffle.random(players.get(whom).hc);
                                players.get(whom).hc.remove((Integer) h);
                                p.hc.add(h);
                                if (skillId == 148)
                                    print("�ޯ�ʵe:�ǵs�E�E:����:�ǵs�E�E�ޯ�" +
                                                  (new Random().nextInt(6) + 1) + ":" + p.seat + ":" +
                                                  whom);
                                else
                                    print("�ޯ�ʵe:�ǵs�E�E:����:�ǵs�E�E�ޯ�" +
                                                  (new Random().nextInt(6) + 1) + ":" + p.seat + ":" +
                                                  whom);
                                print(whom, "�R�@��P:" + h + ":-1");
                                print("�ʤ@�P�ʵe:0:" + whom + ":h:" + players.get(whom).hc.size() + ":" +
                                              p.seat + ":h:"
                                              + p.hc.size() + ":-1:-1");
                                print(p.seat, "�W�@��P:" + h + ":-1");
                                checkWinByHc(p);
                            }
                        }
                    }
                }
                else if (p.chr.equals("����")) {
                    if (CardData.isTrue(id) && sender == p.seat && getter != sender && !p.hc.isEmpty()) {
                        if (askUseSkill(p, 133)) {
                            print(p.seat, "����ʧ@");
                            printEx(p.seat, "������r:�е���" + p.getName() + "�ާ@");
                            ask_th = new MessageTimer(p.seat, "��ܤ�P:-1", wt, "��ܤ�P");
                            print(p.seat, "��@�i��P:" + CardData.getCardsByColor(p.hc, "k") +
                                    ":false:��ܤ@�i��P");
                            ask_th.start();
                            int h = Integer.parseInt(ask_th.get());
                            if (h != -1) {
                                print(p.seat, "����ʧ@");
                                p.hc.remove((Integer) h);
                                players.get(getter).itl.add(h);
                                print("������r:-1");
                                print("�ޯ�ʵe:����:�����äM:���������äM" + (new Random().nextInt(2) + 1) +
                                              ":" + p.seat + ":" + getter);
                                print(p.seat, "�R�@��P:" + h + ":-1");
                                print("�ʤ@�P�ʵe:" + h + ":" + p.seat + ":h:" + p.hc.size() + ":" +
                                              getter + ":k:"
                                              + CardData.getColorCount(players.get(getter).itl, "k") +
                                              ":������:-1");
                                checkWinOrDead(p.seat, getter, false, h);
                            }
                        }
                    }

                }
                else if (p.chr.equals("¾�~����")) {
                    if (sender == p.seat && CardData.isFalse(id))
                        if (askUseSkill(p, 136)) {
                            print(p.seat, "����ʧ@");
                            print("�ޯ�ʵe:¾�~����:�s��:¾�~����s��" + (new Random().nextInt(2) + 1));
                            try {
                                int draw = drawcard();
                                p.hc.add(draw);
                                print("��P�ʵe:" + p.seat + ":1:" + CardData.getColorCount(p.hc, "k"));
                                print(p.seat, "�W�@��P:" + draw + ":-1");
                            } catch (NoCardException e) {
                                e.printStackTrace();
                            }
                            printEx(p.seat, "������r:�е���" + p.getName() + "�ާ@");
                            if (!p.hc.isEmpty() && players.get(getter).isAlive == 1) {
                                ask_th = new MessageTimer(p.seat, "��ܤ�P:-1", wt, "��ܤ�P");
                                print(p.seat, "��@�i��P:" + CardData.getCardsByColor(p.hc, "k") +
                                        ":false:��ܤ@�i��P");
                                ask_th.start();
                                int h = Integer.parseInt(ask_th.get());
                                print("������r:-1");
                                if (h != -1) {
                                    print("������r:-1");
                                    print(p.seat, "����ʧ@");
                                    p.hc.remove((Integer) h);
                                    players.get(getter).itl.add(h);
                                    print(p.seat, "�R�@��P:" + h + ":-1");
                                    print("�ʤ@�P�ʵe:" + h + ":" + p.seat + ":h:" + p.hc.size() + ":" +
                                                  getter + ":k:"
                                                  +
                                                  CardData.getColorCount(players.get(getter).itl, "k") +
                                                  ":������:-1");
                                    checkWinOrDead(p.seat, getter, false, h);
                                }
                            }
                        }
                }
                else if (p.chr.equals("���p�U")) {
                    if (CardData.isFalse(id) && CardData.getColorCount(players.get(getter).itl, "�u") >
                            0 && sender == p.seat)
                        if (askUseSkill(p, 147)) {
                            print(p.seat, "����ʧ@");
                            printEx(p.seat, "������r:�е���" + p.getName() + "�ާ@");
                            ask_th = new MessageTimer(p.seat, "��ܱ���:-1", wt, "��ܱ���");
                            print(p.seat,
                                  "��@�i����:" + CardData.getCardsByColor(players.get(getter).itl, "r")
                                          + CardData.getCardsByColor(players.get(getter).itl, "b") +
                                          ":false:��ܤ@�i����");
                            ask_th.start();
                            int h = Integer.parseInt(ask_th.get());
                            print(p.seat, "����ʧ@");
                            if (h != -1) {
                                String pls = "";
                                for (Player $p : getAlivePlayers())
                                    pls += $p.seat + ",";
                                ask_th = new MessageTimer(p.seat, "��ܪ��a:-1", wt, "��ܪ��a");
                                print(p.seat, "��@�쪱�a:" + pls + ":false:��ܭn���P�����a");
                                ask_th.start();
                                int pl = Integer.parseInt(ask_th.get());
                                if (pl != -1) {
                                    print("������r:-1");
                                    print(p.seat, "����ʧ@");
                                    print("�ޯ�ʵe:���p�U:����:���p�U����" +
                                                  (new Random().nextInt(2) + 1) + ":" + p.seat + ":" +
                                                  getter);
                                    players.get(getter).itl.remove((Integer) h);
                                    players.get(pl).hc.add(h);
                                    print("�ʤ@�P�ʵe:" + h + ":" + getter + ":" +
                                                  CardData.getCardColor(h) + ":"
                                                  + CardData.getColorCount(players.get(getter).itl,
                                                                           CardData.getCardColor(h)) +
                                                  ":"
                                                  + pl + ":h:" + players.get(pl).hc.size() + ":-1:-1");
                                    print(pl, "�W�@��P:" + h + ":-1");
                                    listening();
                                }
                            }
                        }
                }
            }
        }

        public boolean startUsingCard() {
            // ��p�⧹����A�I�s����k�A�i�D�j�a�i�H�~��ϥΥd���A�վ�sit���A�A�վ�canUseCard�A�i�D�j�a�i�Τ�P(�Y�i�X�P)�ú�ť�A�M��Ұʭp�ɾ�
            switch (stage) {
            case "I":
                print("sit:34");
                break;
            case "pas":
                print("sit:36");
                break;
            case "arr":
                print("sit:37");
                break;
            case "II":
                print("sit:39");
                break;
            default:
                new LogicException("���~��stage: " + stage).printStackTrace();
                break;
            }
            canUseCard = true;
            synchronized ($rc) {
                if (getActivePlayers().isEmpty())
                    return false;
                else {
                    printHcCanUse();
                    listen("���X�P");
                    for (Player pl : getAliveOnlinePlayers())
                        if (pl.status == 2)
                            call(pl.seat, "���X�P");
                    time_th = new Thread(useCard_run, "�ۥѥX�P�p�ɾ�");
                    time_th.start();
                    return true;
                }
            }
        }

        public void stopUsingCard() {
            // ��@�쪱�a�X�P�ΨϥΧޯ�ɡA�I�s����k�A�����p�ɰ�����A�վ�canUseCard�A�i�D�j�a����ʧ@�A�è�s����status
            time_th.interrupt();
            canUseCard = false;
            print("����ʧ@");
            for (Player pl : players) {
                pl.sit = 0;
                pl.cardId = -1;
            }
        }

        public void testBlock() {

        }

        public int useSkillWhenCal(int cardOrSkillId, int who, int whom, String type, int cc)
                throws WinException {

            for (Player p : getAlivePlayersByPri()) {

                if (p.isChrCov) {
                    if (queueOldSkill[cc] != null) {
                        if (queueOldSkill[cc].p == p.seat)
                            switch (queueOldSkill[cc].skillId) {
                            case 100:
                                p.isChrCov = false;
                                p.hasPrepareOldSkill = false;
                                print("½�}����:" + p.seat + ":�Ѱ�");
                                print("�ޯ�ʵe:�Ѱ�:�N�p:�Ѱ��N�p" + (new Random().nextInt(2) + 1));
                                try {
                                    int h0 = drawcard();
                                    p.hc.add(h0);
                                    try {
                                        int h1 = drawcard();
                                        p.hc.add(h1);
                                        print("��P�ʵe:" + p.seat + ":2:" + p.hc.size());
                                        print(p.seat, "�W�h��P:" + h0 + "," + h1 + ":-1");
                                    } catch (NoCardException e) {
                                        e.printStackTrace();
                                        print("��P�ʵe:" + p.seat + ":1:" + p.hc.size());
                                        print(p.seat, "�W�@��P:" + h0 + ":-1");
                                    }
                                } catch (NoCardException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case 103:
                                p.isChrCov = false;
                                p.hasPrepareOldSkill = false;
                                print("½�}����:" + p.seat + ":�Ѻj");
                                print("�ޯ�ʵe:�Ѻj:�N�p:�Ѻj�N�p" + (new Random().nextInt(2) + 1));
                                try {
                                    int h0 = drawcard();
                                    p.hc.add(h0);
                                    try {
                                        int h1 = drawcard();
                                        p.hc.add(h1);
                                        print("��P�ʵe:" + p.seat + ":2:" + p.hc.size());
                                        print(p.seat, "�W�h��P:" + h0 + "," + h1 + ":-1");
                                    } catch (NoCardException e) {
                                        e.printStackTrace();
                                        print("��P�ʵe:" + p.seat + ":1:" + p.hc.size());
                                        print(p.seat, "�W�@��P:" + h0 + ":-1");
                                    }
                                } catch (NoCardException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case 105:
                                p.isChrCov = false;
                                p.hasPrepareOldSkill = false;
                                print("½�}����:" + p.seat + ":�Ѫ�");
                                print("�ޯ�ʵe:�Ѫ�:���O��:�Ѫ����O��");
                                try {
                                    int h0 = drawcard();
                                    p.hc.add(h0);
                                    try {
                                        int h1 = drawcard();
                                        p.hc.add(h1);
                                        try {
                                            int h2 = drawcard();
                                            p.hc.add(h2);
                                            try {
                                                int h3 = drawcard();
                                                p.hc.add(h3);
                                                try {
                                                    int h4 = drawcard();
                                                    p.hc.add(h4);
                                                    print("��P�ʵe:" + p.seat + ":5:" + p.hc.size());
                                                    print(p.seat,
                                                          "�W�h��P:" + h0 + "," + h1 + "," + h2 + "," +
                                                                  h3 + "," + h4
                                                                  + ":-1");
                                                } catch (NoCardException e) {
                                                    e.printStackTrace();
                                                    print("��P�ʵe:" + p.seat + ":4:" + p.hc.size());
                                                    print(p.seat,
                                                          "�W�h��P:" + h0 + "," + h1 + "," + h2 + "," +
                                                                  h3 + ":-1");
                                                }
                                            } catch (NoCardException e) {
                                                e.printStackTrace();
                                                print("��P�ʵe:" + p.seat + ":3:" + p.hc.size());
                                                print(p.seat,
                                                      "�W�h��P:" + h0 + "," + h1 + "," + h2 + ":-1");
                                            }
                                        } catch (NoCardException e) {
                                            e.printStackTrace();
                                            print("��P�ʵe:" + p.seat + ":2:" + p.hc.size());
                                            print(p.seat, "�W�h��P:" + h0 + "," + h1 + ":-1");
                                        }
                                    } catch (NoCardException e) {
                                        e.printStackTrace();
                                        print("��P�ʵe:" + p.seat + ":1:" + p.hc.size());
                                        print(p.seat, "�W�@��P:" + h0 + ":-1");
                                    }
                                } catch (NoCardException e) {
                                    e.printStackTrace();
                                }
                                ask_th = new MessageTimer(p.seat, "��ܦh�i��P:-1", 16000, "��ܦh�i��P");
                                if (p.hc.size() > 1) { // ��ܨ�i��^
                                    print(p.seat, "��h�i��P:" + CardData.getCards(p.hc)
                                            +
                                            ":true:2:2:16000:��ܨ�i��P��^�P�w���A�A���I�諸�P�|��b�Ʈw���W��");
                                    ask_th.start();
                                    String cs = ask_th.get();
                                    int h0, h1;
                                    if (cs.equals("-1")) {
                                        int[] random = Shuffle.getRandom(2, p.hc.size());
                                        h0 = p.hc.get(random[0]);
                                        h1 = p.hc.get(random[1]);
                                    }
                                    else {
                                        String[] c = cs.split(",");
                                        h0 = Integer.parseInt(c[0]);
                                        h1 = Integer.parseInt(c[1]);
                                    }
                                    p.hc.remove((Integer) h0);
                                    p.hc.remove((Integer) h1);
                                    mt.add(0, h1);
                                    mt.add(0, h0);
                                    print(p.seat, "�R�h��P:" + h0 + "," + h1 + ":-1");
                                    print("�ʦh�P�ʵe:0,0:" + p.seat + "," + p.seat + ":h,h:" +
                                                  p.hc.size() + "," + p.hc.size()
                                                  + ":1000,1000:-1,-1:-1,-1:-1:-1");
                                }
                                else if (!p.hc.isEmpty()) { // ������^
                                    print(p.seat, "��h�i��P:" + CardData.getCards(p.hc)
                                            +
                                            ":true:1:1:16000:��ܨ�i��P��^�P�w���A�A���I�諸�P�|��b�Ʈw���W��A�p�G�A����P������i�A�N�A�Ҧ���P��^�P�w��");
                                    ask_th.start();
                                    ask_th.get();
                                    int h = p.hc.get(0);
                                    p.hc.clear();
                                    mt.add(0, h);
                                    print(p.seat, "�R�@��P:" + h + ":-1");
                                    print("�ʤ@�P�ʵe:0:" + p.seat + ":h:0:1000:-1:-1:-1:-1");
                                }
                                checkWinByHc(p);
                                break;
                            }
                    }
                }
                else if (cardOrSkillId < 100)
                    switch (p.chr) {
                    case "Ķ�q��":
                        if (type.equals("�}Ķ")) {
                            if (askUseSkill(p, 108)) { // �}�x
                                p.isChrCov = true;
                                print("�ޯ�ʵe:Ķ�q��:�}�x:Ķ�q���}�x" + (new Random().nextInt(2) + 1));
                                print("�\�񨤦�:" + p.seat + ":Ķ�q��");
                            }
                            else
                                print(p.seat, "����ʧ@");
                        }
                        break;
                    case "���j":
                        if (type.equals("�ձ�") && p.seat == who && !players.get(whom).hc.isEmpty()) {
                            if (askUseSkill(p, 119)) { // ����
                                int h = Shuffle.random(players.get(whom).hc);
                                players.get(whom).hc.remove((Integer) h);
                                p.hc.add(h);
                                print("�ޯ�ʵe:���j:����:���j����" + (new Random().nextInt(2) + 1) + ":" +
                                              p.seat + ":" + whom);
                                print(whom, "�R�@��P:" + h + ":-1");
                                print("�ʤ@�P�ʵe:0:" + whom + ":h:" + players.get(whom).hc.size() + ":" +
                                              who + ":h:"
                                              + players.get(who).hc.size() + ":-1:-1");
                                print(who, "�W�@��P:" + h + ":-1");
                            }
                            else
                                print(p.seat, "����ʧ@");
                        }
                        break;
                    case "�P�R����":
                        if ((type.equals("��w") || type.equals("�ժ����s")) && p.seat == who) {
                            if (askUseSkill(p, 123)) { // ��u�ݳ�
                                print(p.seat, "����ʧ@");
                                print("������r:-1");
                                try {
                                    int h0 = drawcard();
                                    print("�ޯ�ʵe:�P�R����:��u�ݳ�:�P�R������u�ݳ�");
                                    try {
                                        int h1 = drawcard();
                                        p.hc.add(h0);
                                        p.hc.add(h1);
                                        print("��P�ʵe:" + who + ":2:" + p.hc.size());
                                        print(who, "�W�h��P:" + h0 + "," + h1 + ":-1");
                                    } catch (NoCardException e) {
                                        p.hc.add(h0);
                                        print("��P�ʵe:" + who + ":1:" + p.hc.size());
                                        print(who, "�W�@��P:" + h0 + ":-1");
                                    }
                                } catch (NoCardException e) {
                                    e.printStackTrace();
                                }
                                if (!p.hc.isEmpty()) {
                                    print(who, "��@�i��P:" + CardData.getCards(p.hc) +
                                            ":true:��ܤ@�i��P��^�P�w��");
                                    ask_th = new MessageTimer(who, "��ܤ�P:-1", wt, "��ܤ�P");
                                    ask_th.start();
                                    int h = Integer.parseInt(ask_th.get());
                                    if (h == -1)
                                        h = Shuffle.random(p.hc);
                                    p.hc.remove((Integer) h);
                                    mt.add(0, h);
                                    print(who, "�R�@��P:" + h + ":-1");
                                    print("�ʤ@�P�ʵe:0:" + who + ":h:" + p.hc.size() +
                                                  ":1000:-1:-1:-1:-1");
                                }
                            }
                            else {
                                print(p.seat, "����ʧ@");
                                print("������r:-1");
                            }

                        }
                        break;
                    case "�m��":
                        if (type.equals("��w") && p.seat == who) {
                            if (askUseSkill(p, 126)) { // �p��
                                print(p.seat, "����ʧ@");
                                print("������r:-1");
                                try {
                                    int h0 = drawcard();
                                    print("�ޯ�ʵe:�m��:�p��:�m���p��" + (new Random().nextInt(2) + 1));
                                    try {
                                        int h1 = drawcard();
                                        p.hc.add(h0);
                                        p.hc.add(h1);
                                        print("��P�ʵe:" + who + ":2:" + p.hc.size());
                                        print(p.seat, "�W�h��P:" + h0 + "," + h1 + ":-1");
                                    } catch (NoCardException e) {
                                        p.hc.add(h0);
                                        print("��P�ʵe:" + who + ":1:" + p.hc.size());
                                        print(p.seat, "�W�@��P:" + h0 + ":-1");
                                    }
                                } catch (NoCardException e) {
                                    e.printStackTrace();
                                }
                                if (!p.hc.isEmpty()) {
                                    print(p.seat, "��@�i��P:" + CardData.getCards(p.hc) +
                                            ":true:��ܤ@�i��P�񵹥t�@�쪱�a");
                                    wait(p);
                                    ask_th = new MessageTimer(p.seat, "��ܤ�P:-1", wt, "��ܤ�P");
                                    ask_th.start();
                                    int h = Integer.parseInt(ask_th.get());
                                    print(p.seat, "����ʧ@");
                                    if (h == -1)
                                        h = Shuffle.random(p.hc);
                                    print(p.seat, "�R�@��P:" + h + ":-1");
                                    String pls = "";
                                    for (Player $p : getAlivePlayers())
                                        if ($p.seat != p.seat)
                                            pls += $p.seat + ",";
                                    print(p.seat, "��@�쪱�a:" + pls + ":true:��ܤ@�i��P�����t�@�쪱�a");
                                    ask_th = new MessageTimer(who, "��ܪ��a:-1", wt, "��ܪ��a");
                                    ask_th.start();
                                    int pl = Integer.parseInt(ask_th.get());
                                    print(p.seat, "����ʧ@");
                                    print("������r:-1");
                                    if (pl == -1) {
                                        int ran = new Random().nextInt(players.size());
                                        pl = getAlivePlayers().get(ran).seat;
                                    }
                                    p.hc.remove((Integer) h);
                                    players.get(pl).hc.add(h);
                                    print("�ʤ@�P�ʵe:0:" + who + ":h:" + p.hc.size() + ":" + pl + ":h:"
                                                  + players.get(pl).hc.size() + ":-1:-1");
                                    print(pl, "�W�@��P:" + h + ":-1");
                                }
                            }
                            else {
                                print(p.seat, "����ʧ@");
                                print("������r:-1");
                            }
                        }
                        break;
                    case "�ֺ�����":
                        if (type.equals("�ձ�") && !p.hc.isEmpty()) {
                            if (askUseSkill(p, 140)) { // �u��
                                print(p.seat, "����ʧ@");
                                print(p.seat,
                                      "��@�i��P:" + CardData.getCards(p.hc) + ":false:��ܤ@�i�n��󪺤�P");
                                ask_th = new MessageTimer(p.seat, "��ܤ�P:-1", wt, "��ܤ�P");
                                ask_th.start();
                                int h = Integer.parseInt(ask_th.get());
                                print(p.seat, "����ʧ@");
                                print("������r:-1");
                                if (h != -1) {
                                    p.hc.remove((Integer) h);
                                    trash.add(h);
                                    print("�ޯ�ʵe:�ֺ�����:�u��:�ֺ������u��" +
                                                  (new Random().nextInt(2) + 1));
                                    print(p.seat, "�R�@��P:" + h + ":-1");
                                    print("�ʤ@�P�ʵe:" + h + ":" + p.seat + ":h:" + p.hc.size() +
                                                  ":999:-1:-1:-1:-1");
                                    ask_th = new MessageTimer(p.seat, "�[�ݵ���", wt, "�[�ݵ���");
                                    print(p.seat, "�[�ݥd�P:" + cardOrSkillId + ":����O�@�i�ձ�");
                                    ask_th.start();
                                    ask_th.get();
                                }
                            }
                            else {
                                print(p.seat, "����ʧ@");
                                print("������r:-1");
                            }
                            print(p.seat, "����ʧ@");
                        }
                        else {
                            print(p.seat, "����ʧ@");
                            print("������r:-1");
                        }
                        break;
                    case "�����B��":
                        if (type.equals("�ձ�") && p.seat == who) {
                            if (askUseSkill(p, 141)) { // �j�𦳦�
                                print(p.seat, "����ʧ@");
                                print("������r:-1");
                                try {
                                    int h0 = drawcard();
                                    print("�ޯ�ʵe:�����B��:�j�𦳦�:�����B���j�𦳦�" +
                                                  (new Random().nextInt(2) + 1));
                                    try {
                                        int h1 = drawcard();
                                        p.hc.add(h0);
                                        p.hc.add(h1);
                                        print("��P�ʵe:" + who + ":2:" + p.hc.size());
                                        print(who, "�W�h��P:" + h0 + "," + h1 + ":-1");
                                    } catch (NoCardException e) {
                                        p.hc.add(h0);
                                        print("��P�ʵe:" + who + ":1:" + p.hc.size());
                                        print(who, "�W�@��P:" + h0 + ":-1");
                                    }
                                } catch (NoCardException e) {
                                    e.printStackTrace();
                                }
                                if (!p.hc.isEmpty()) {
                                    printEx(p.seat, "������r:�е���" + p.getName() + "�ާ@");
                                    print(who, "��@�i��P:" + CardData.getCards(p.hc) +
                                            ":true:��ܤ@�i��P��^�P�w��");
                                    ask_th = new MessageTimer(who, "��ܤ�P:-1", wt, "��ܤ�P");
                                    ask_th.start();
                                    int h = Integer.parseInt(ask_th.get());
                                    if (h == -1)
                                        h = Shuffle.random(p.hc);
                                    p.hc.remove((Integer) h);
                                    mt.add(0, h);
                                    print(who, "�R�@��P:" + h + ":-1");
                                    print("�ʤ@�P�ʵe:0:" + who + ":h:" + p.hc.size() +
                                                  ":1000:-1:-1:-1:-1");
                                }
                            }
                            else
                                print(p.seat, "����ʧ@");
                        }
                        break;
                    }
            }

            return -1;

        }

        public void wait(int p) {
            wait(players.get(p));
        }

        public void wait(Player p) {
            printEx(p.seat, "������r:�е���" + p.getName() + ":�ާ@");
        }

    }

    class User {

        public User(Socket s) {
            sc = s;
            try {
                br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
                pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sc.getOutputStream())));
                pos = new PipedOutputStream();
                pout = new PrintWriter(new BufferedWriter(new OutputStreamWriter(pos)));
            } catch (IOException e) {
                e.printStackTrace();
            }

            logic();

        }
        public int UID;
        public BufferedReader br;
        public boolean canSpeak = true;
        public boolean canUseSound = true;
        public boolean canWarn = true;
        public Player pl;
        public PipedOutputStream pos;
        public PrintWriter pw, pout;
        public Room room;
        public Socket sc;
        public String username;

        public void getOut() {
            int seat = room.waitingplayers.indexOf(pl);
            room.waitingplayers.get(seat).isReady = false;
            room.waitingplayers.set(seat, null);
            room.plyCount--;
            boolean nobody = false;
            if (room.chief == pl.user.UID) { // �ಾ�Ǫ�
                if (room.plyCount == 0) {
                    nobody = true;
                    room.name = "�ũ�";
                    room.chief = -1;
                }
                else { // �ಾ�Ǫ�
                    int newChiefSeat = -1;
                    for (int i = 0; i < 9; i++) // �M��s�Ǫ�
                        if (room.waitingplayers.get(i) != null) {
                            newChiefSeat = i;
                            room.chief = room.waitingplayers.get(i).user.UID;
                            break;
                        }
                    for (int i = 0; i < 9; i++) {
                        Player p = room.waitingplayers.get(i);
                        if (p != null)
                            p.print("�����ܧ�:" + (newChiefSeat == i) + ":" + newChiefSeat);
                    }
                }
            }
            if (!nobody) {
                room.print("�ж����H:" + seat + ":-1:false:false"); // �i�D��ж���L���a�L�w�h�X
                room.print("�t�λ���:���a " + username + " ���}�ж��C");
            }
            lobbyPrint("�ж���T:" + room.id + ":" + room.name + ":" + room.getStatus() + ":" +
                               room.plyCount + " / "
                               + room.getMax());
            users.put(UID, this);
            room = null;
            setPlayer(null);
            print("�i�J�j�U");
            print("sit:1");
            for (int r = 0; r < 11; r++) { // �i�D�����a�ж��C���B
                Room rm = rooms.get(r);
                print("�ж���T:" + r + ":" + rm.name + ":" + rm.getStatus() + ":" + rm.plyCount + " / " +
                              rm.getMax());
            }
        }

        public void logic() {

            Runnable login = () -> {
                String readed = null;
                try {
                    while (true) {
                        try {
                            readed = br.readLine();
                            if (readed == null)
                                throw new IOException();
                            System.out.println("Ū�J�T��:" + readed);

                            if (room != null) {
                                if (room.getStatus() == 2) {
                                    String[] line = readed.split(":");
                                    if (line[0].equals("�D�зǰT��")) {
                                        switch (line[1]) {
                                        case "�[�ݱ���":
                                            // ~:�[�ݱ���:2���ݪ��a�츹
                                            String cs = "";
                                            int whom = Integer.parseInt(line[2]);
                                            if (whom == 100) { // ��P��
                                                for (int id : room.trash)
                                                    cs += id + ",";
                                                print("�[�ݥd�P��T:" + cs + ":��P��]�@" +
                                                              room.trash.size() + "�i�^�A�t���ձ���P"
                                                              + room.testTrash.size() + "�i");
                                            }
                                            else if (whom == 101) { // �P�w
                                                int size = room.mt.size();
                                                cs = "100";
                                                print("�[�ݥd�P��T:" + cs + ":�P�w�]��" + size + "�i�^");
                                            }
                                            else {
                                                for (int id : room.players.get(whom).itl)
                                                    cs += id + ",";
                                                print("�[�ݥd�P��T:" + cs + ":" +
                                                              room.players.get(whom).getName() +
                                                              "��o�������]�@"
                                                              + room.players.get(whom).itl.size() +
                                                              "�i�^");
                                            }
                                            break;
                                        case "�U��":
                                            synchronized (room.$rc) {
                                                pl.status = 2;
                                            }
                                            room.print("���a�U��:" + pl.seat + ":�U��");
                                            if (room.ask_th != null) {
                                                if (room.ask_th.who == pl.seat && room.ask_th.isAlive())
                                                    room.ask_th.replyNow();
                                            }
                                            if (room.time_th != null) {
                                                if (room.time_th.isAlive()) {
                                                    room.pout.println(pl.seat + "$�ϥΤ�P:-1");
                                                    room.pout.flush();
                                                }
                                            }
                                            break;
                                        case "�����U��":
                                            synchronized (room.$rc) {
                                                pl.status = 1;
                                            }
                                            room.print("���a�U��:" + pl.seat + ":�����U��");
                                            break;
                                        case "�`�λy":
                                            if (canUseSound) {
                                                Runnable run = () -> {
                                                    canUseSound = false;
                                                    room.print("�`�λy:" + line[2]);
                                                    try {
                                                        Thread.sleep(6000);
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                    canUseSound = true;
                                                };
                                                new Thread(run).start();
                                            }
                                            break;
                                        case "�C�����":
                                            String txt = line[2].trim();
                                            txt = txt.replaceAll("##", "#");
                                            txt = txt.replaceAll("%%", "%");
                                            txt = txt.replaceAll("%#", ":");
                                            if (!txt.equals(""))
                                                room.print("�C������:" + pl.seat + ":" + txt);
                                            break;
                                        default:
                                            new LogicException("���~���T��: " + readed)
                                                    .printStackTrace();
                                            break;
                                        }
                                    }
                                    else {
                                        room.pout.println(pl.seat + "$" + readed);
                                        room.pout.flush();
                                    }
                                    continue;
                                }
                            }

                            String[] ms = readed.split(":");
                            switch (ms[0]) {
                            case "�n�D�n�J":
                                // �n�J:�b��:�K�X
                                String usnm = ms[1];
                                try (ObjectInputStream ois = new ObjectInputStream(
                                        new FileInputStream("players/" + usnm + ".dll"))) {
                                    UserFile uf = (UserFile) ois.readObject();

                                    if (uf.password.equals(ms[2])) {
                                        boolean isOnline = false;
                                        back:
                                        {
                                            if (users.containsKey(uf.UID)) { // �b���ϥΤ�
                                                isOnline = true;
                                                System.out.println("�j�U���" + uf.username);
                                            }
                                            else {
                                                for (int r = 0; r < 11; r++) {
                                                    for (Player pl : rooms.get(r).players)
                                                        if (pl != null)
                                                            if (pl.user != null)
                                                                if (pl.user.UID == uf.UID) {
                                                                    System.out.println(
                                                                            "�b�ж�" + r + "���" +
                                                                                    uf.username);
                                                                    isOnline = true;
                                                                    break back;
                                                                }
                                                    for (Player pl : rooms.get(r).waitingplayers)
                                                        if (pl != null)
                                                            if (pl.user != null)
                                                                if (pl.user.UID == uf.UID) {
                                                                    isOnline = true;
                                                                    break back;
                                                                }
                                                }
                                            }
                                        }
                                        if (isOnline)
                                            print("�n�J���~:�b���ϥΤ��A�L�k�n�J");
                                        else { // �n�J���\
                                            set(uf);
                                            print("�i�J�j�U");
                                            print("sit:1");
                                            users.put(UID, this);
                                            for (int r = 0; r < 11; r++) {
                                                Room rm = rooms.get(r);
                                                print("�ж���T:" + r + ":" + rm.name + ":" +
                                                              rm.getStatus() + ":" + rm.plyCount
                                                              + " / " + rm.getMax());
                                            }
                                            print("�t�λ���:�i�t�Ρj�w��Ӵ��խ��n��CBeta v0.4�I�A�i�H�b�U����J��Ѥ��e�A�M���Enter�o�e�T���A�Ҧ��b�u���a����ݨ�C");
                                            print("�t�λ���:�i���i�j�̲״��ժ���v0.5�N�|���X�����X�R����A�s�W�C������ѫǡB�C���y���\��A�åB����b�C���ɬݨ��Ҧ��H���ʺ١Cv0.5�w�p8��10��e���X�Cv0.5��N�|���X�������C");
                                            print("�t�λ���:�i���i�j�w�g�״_����ݨ��ۤv�����B�ޯ�ʵe��r�L�k��ܥH�ΨS�����֪�Bug�C");
                                            print("�t�λ���:�i���i�j�w�g�����Ҧ����⪺���K���ȡC");
                                            print("�t�λ���:�i���i�j�p�G�A������ê��a�A��A��ƹ����}�A������ϮɡA�ϥܤ��|�ܦ^���áC�w�g����]�åB�|�b�U���צn�C�o�������v�T�C���C");
                                            print("�t�λ���:�i���i�j�`�N!!!���ê����⦺�`�|�ɭP�Ҧ��H���!!!�幩�b�o��say sorry�A���C�����ɭԤ��n�����S½�}������A�w�g����]�åB�|�b�U���צnQAQ�C");
                                        }
                                    }
                                    else
                                        print("�n�J���~:�K�X���~");
                                } catch (FileNotFoundException e) {
                                    print("�n�J���~:�b�����s�b");
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case "�i�J�ж�":
                                // �i�J�ж�:�ж��츹
                                synchronized (this) {
                                    int roomId = Integer.parseInt(ms[1]);
                                    Room theroom = rooms.get(roomId);

                                    // ���T�w�O�_���H�w�b�ж�����
                                    boolean hasIn = false;
                                    for (Player pl : theroom.waitingplayers)
                                        if (pl != null)
                                            if (pl.user != null)
                                                if (pl.user.UID == UID) {
                                                    hasIn = true;
                                                    break;
                                                }
                                    if (hasIn)
                                        break;

                                    if (theroom.getStatus() == 0 &&
                                            theroom.plyCount < theroom.getMax()) {
                                        theroom.plyCount++;
                                        users.remove(UID);
                                        Player player = new Player(this);
                                        room = theroom;
                                        pl = player;
                                        if (theroom.chief == -1) {
                                            theroom.chief = player.user.UID;
                                            room.name = this.username + "���ж�";
                                            player.isReady = true;
                                        }
                                        int seat = -1;
                                        for (int i = 0; i < 9; i++)
                                            if (!theroom.isBlocked.get(i) && theroom.waitingplayers.get(
                                                    i) == null) {
                                                theroom.waitingplayers.set(i, player);
                                                seat = i;
                                                break;
                                            }
                                        print("�i�J�ж�:" + (theroom.chief == UID) + ":" + roomId);
                                        print("sit:2");

                                        for (int i = 0; i < 9; i++) { // �i�D�����a�ж����H
                                            String name = null;
                                            boolean isReady = false;
                                            if (theroom.isBlocked.get(i))
                                                name = "-2";
                                            else if (theroom.waitingplayers.get(i) == null)
                                                name = "-1";
                                            else {
                                                name = theroom.waitingplayers.get(i).user.username;
                                                isReady = theroom.waitingplayers.get(i).isReady;
                                            }
                                            if (theroom.waitingplayers.get(i) == null)
                                                print("�ж����H:" + i + ":" + name + ":false:" + isReady);
                                            else
                                                print("�ж����H:" + i + ":" + name + ":"
                                                              + (theroom.chief ==
                                                        theroom.waitingplayers.get(i).user.UID) + ":"
                                                              + isReady);
                                        }

                                        for (int p = 0; p < 9; p++) { // �i�D���ж���L�H�T��
                                            Player pl = room.waitingplayers.get(p);
                                            if (pl != null)
                                                if (p != seat) {
                                                    room.waitingplayers.get(p).print(
                                                            "�ж����H:" + seat + ":" + this.username
                                                                    + ":"
                                                                    + (theroom.chief ==
                                                                    theroom.waitingplayers.get(
                                                                            seat).user.UID)
                                                                    + ":false");
                                                }
                                        }
                                        theroom.print("�t�λ���:���a " + username + " �i�J�ж��C");
                                        lobbyPrint("�ж���T:" + room.id + ":" + room.name + ":" +
                                                           room.getStatus() + ":"
                                                           + room.plyCount + " / " + room.getMax());

                                    }
                                    else
                                        new LogicException("�ж��w���o���i�I��").printStackTrace();
                                }
                                break;
                            case "��^�j�U":
                                synchronized (this) {
                                    getOut();
                                }
                                break;
                            case "�ǳ�":
                                synchronized (this) {
                                    pl.isReady = !pl.isReady;
                                    room.print("���a�ǳ�:" + room.waitingplayers.indexOf(pl) + ":" +
                                                       pl.isReady);
                                }
                                break;
                            case "���a����":
                                // ���a����:�����̦츹
                                synchronized (this) {
                                    if (pl.user.UID == room.chief) {
                                        int whom = Integer.parseInt(ms[1]);
                                        if (pl != room.waitingplayers.get(whom)) { // �����ۤv
                                            if (room.waitingplayers.get(whom) == null) { // ����
                                                boolean b = !room.isBlocked.get(whom);
                                                if (b) { // ����
                                                    if (room.getMax() > 2) {
                                                        room.isBlocked.set(whom, b);
                                                        room.print(
                                                                "�ж����H:" + whom + ":-2:false:false");
                                                        lobbyPrint(
                                                                "�ж���T:" + room.id + ":" + room.name +
                                                                        ":" + room.getStatus()
                                                                        + ":" + room.plyCount + " / " +
                                                                        room.getMax());
                                                    }
                                                    else if (canWarn) {
                                                        Runnable warn_run = () -> {
                                                            canWarn = false;
                                                            try {
                                                                Thread.sleep(1000);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                            canWarn = true;
                                                        };
                                                        print("�t�λ���:�ܤ֭n�}�Ҥ@�ӹC���y��C");
                                                        new Thread(warn_run).start();
                                                    }
                                                }
                                                else { // �ѫ�
                                                    room.isBlocked.set(whom, b);
                                                    room.print("�ж����H:" + whom + ":-1:false:false");
                                                    lobbyPrint(
                                                            "�ж���T:" + room.id + ":" + room.name + ":" +
                                                                    room.getStatus()
                                                                    + ":" + room.plyCount + " / " +
                                                                    room.getMax());
                                                }
                                                // TODO
                                            }
                                            else // ��X
                                                room.waitingplayers.get(whom).user
                                                        .getOut(); // �]�t�j�U�T���F
                                        }
                                    }
                                    else {
                                        new LogicException("���O�������H�Q��X���a").printStackTrace();
                                    }
                                }
                                break;
                            case "�C���}�l":
                                synchronized (this) {
                                    if (pl.user.UID == room.chief) {
                                        System.out.println("room.plyCount = " + room.plyCount);
                                        if (room.plyCount > 1) { // �n�ܤ�2�H
                                            boolean canStart = true;
                                            for (int i = 0; i < 9; i++)
                                                if (room.waitingplayers.get(i) != null)
                                                    if (!room.waitingplayers.get(i).isReady
                                                            && room.waitingplayers.get(i).user.UID !=
                                                            room.chief) {
                                                        canStart = false;
                                                        break;
                                                    }

                                            if (canStart) {
                                                lobbyPrint(
                                                        "�ж���T:" + room.id + ":" + room.name + ":2:" +
                                                                room.plyCount
                                                                + " / " + room.getMax());
                                                room.e = new WinException();
                                                room.stage = "chochr";
                                                Runnable startGame = () -> {
                                                    room.game();
                                                };
                                                room.roomThread = new Thread(startGame);
                                                room.roomThread.start();
                                            }
                                            else if (canWarn) {
                                                Runnable warn_run = () -> {
                                                    canWarn = false;
                                                    try {
                                                        Thread.sleep(1000);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    canWarn = true;
                                                };
                                                print("�t�λ���:�е��ԩҦ����a�ǳƧ����C");
                                                new Thread(warn_run).start();
                                            }
                                        }
                                        else if (canWarn) {
                                            Runnable warn_run = () -> {
                                                canWarn = false;
                                                try {
                                                    Thread.sleep(1000);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                canWarn = true;
                                            };
                                            print("�t�λ���:���a�H�Ƥ����C");
                                            new Thread(warn_run).start();
                                        }
                                    }
                                    else {
                                        new LogicException("���O�������H�Q�n�}�l�C��").printStackTrace();
                                    }
                                }
                                break;
                            case "�j�U���":
                                if (canSpeak) {
                                    Runnable speak_run = () -> {
                                        canSpeak = false;
                                        try {
                                            Thread.sleep(1000);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        canSpeak = true;
                                    };
                                    ms[1] = ms[1].replaceAll("##", "#");
                                    ms[1] = ms[1].replaceAll("%%", "%");
                                    ms[1] = ms[1].replaceAll("%#", ":");
                                    // if (ms[1].length() > 15)
                                    // ms[1] = ms[1].substring(0, 15);
                                    lobbyPrint("���a����:" + username + ":" + ms[1]);
                                    for (int i = 0; i < 11; i++) {
                                        Room room = rooms.get(i);
                                        if (room.getStatus() == 0)
                                            room.print("���a����:" + username + ":" + ms[1]);
                                    }
                                    new Thread(speak_run).start();
                                }
                                break;
                            default:
                                new LogicException("���~���T��: " + readed).printStackTrace();
                                break;
                            }
                        } catch (IOException e) {
                            throw e;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    if (e.getMessage() == null)
                        System.out.println("���a " + username + " ���u");
                    else {
                        System.out.println("���a " + username + " �����`�_�u");
                        e.printStackTrace();
                    }
                    if (room != null && pl != null) {

                        if (room.getStatus() == 2) {
                            if (room.time_th.isAlive()) {
                                if (room.time_th.getName().equals("�ۥѥX�P�p�ɾ�")) {
                                    room.pout.println(pl.seat + "$�ϥΤ�P:-1");
                                    room.pout.flush();
                                }
                                else if (room.time_th.getName().equals("��ܨ���p�ɾ�")) {
                                    room.pout.println(pl.seat + "$���a�_�u");
                                    room.pout.flush();
                                }
                            }
                            if (room.ask_th != null)
                                if (room.ask_th.who == pl.seat)
                                    room.ask_th.replyNow();
                            pl.status = 3; // TODO �q���_�u
                            room.print("���a�U��:" + pl.seat + ":���u");
                        }
                        int seat = room.waitingplayers.indexOf(pl);
                        room.plyCount--;
                        room.waitingplayers.set(seat, null);
                        if (room.chief == UID) {
                            int chiefSeat = -1;
                            for (Player wp : room.waitingplayers) {
                                if (wp != null)
                                    if (wp.user != null)
                                        if (wp.user.UID != room.chief) {
                                            room.chief = wp.user.UID;
                                            chiefSeat = room.waitingplayers.indexOf(wp);
                                            break;
                                        }
                            }
                            if (room.getStatus() != 2)
                                for (Player $p : room.waitingplayers)
                                    if ($p != null)
                                        if ($p.user != null)
                                            $p.print("�����ܧ�:" + (UID == room.chief) + ":" + chiefSeat);
                        }
                        if (room.getStatus() != 2) {
                            if (room.plyCount == 0)
                                room.name = "�ũ�";
                            else {
                                room.print("�ж����H:" + seat + ":-1:false:false");
                                room.print("�t�λ���:���a " + username + " ���}�ж�");
                            }
                        }
                        lobbyPrint("�ж���T:" + room.id + ":" + room.name + ":" + room.getStatus() + ":" +
                                           room.plyCount + " / "
                                           + room.getMax());
                    }

                    users.remove((Integer) UID);
                    if (pl != null) {
                        pl.status = 3;
                        pl.user = null;
                        room = null;
                        pl = null;
                    }
                    pw.close();
                    try {
                        br.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        sc.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            };

            new Thread(login, "Ū�J�����").start();

        }

        public void print(String str) {
            pw.println(str);
            pw.flush();
            if (username == null)
                System.out.println("�V�@�ӳX�ȵo�e�T��: " + str);
            else
                System.out.println("�V " + username + " �o�e�T��: " + str);
        }

        public void set(UserFile uf) {
            username = uf.username;
            UID = uf.UID;
        }

        public Player setPlayer(Player p) {
            if (p != null) {
                pl = p;
                pl.user = this;
            }
            else {
                pl = null;
            }
            return pl;
        }

    }

    public static String aylMes(boolean person, String mess) {
        String[] ms0 = mess.split("\\$");
        if (ms0.length > 1) {
            if (person)
                return ms0[0];
            else
                return ms0[1];
        }
        else {
            if (person)
                return null;
            else
                return mess;
        }
    }

    public static void main(String[] args) {
        new Server();
    }
    public ArrayList<Room> rooms = new ArrayList<>();
    ServerSocket ss;
    public HashMap<Integer, User> users = new HashMap<>();

    public void lobbyPrint(String ms) {
        for (User u : users.values())
            u.print(ms);
    }

}
