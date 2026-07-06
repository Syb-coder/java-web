package com.example.java3.init;

import com.example.java3.model.Fortune;
import com.example.java3.model.FortuneLevel;
import com.example.java3.repository.FortuneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 签文库初始化：应用启动时插入 30 支签
 */
@Configuration
public class FortuneDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(FortuneDataInitializer.class);

    /** 30 支签数据：number, title, poem, interpretation, level */
    private static final Object[][] FORTUNES = {
            // 上上签 ×3
            {1, "紫气东来", "春风得意马蹄疾，一日看尽长安花。\n直上青云九万里，金榜题名满天下。", "所求皆遂，万事亨通。宜把握良机，乘势而上。", FortuneLevel.SUPREME},
            {2, "金龙献瑞", "龙腾九霄云开处，瑞气千条照玉阶。\n福禄寿喜齐临门，富贵荣华自此来。", "大吉大利，福运齐至。心愿可成，前途光明。", FortuneLevel.SUPREME},
            {3, "锦上添花", "花开富贵满堂红，喜气洋洋入帘栊。\n锦上添花福更厚，更上层楼步步荣。", "喜事重重，福上加福。宜广结善缘，分享喜悦。", FortuneLevel.SUPREME},
            // 上签 ×6
            {4, "青云直上", "鲲鹏展翅九万里，一飞冲天志未休。\n前程似锦无人挡，功成名就自然酬。", "所谋可成，前路顺遂。宜坚定志向，勇往直前。", FortuneLevel.GOOD},
            {5, "月明星稀", "皎皎明月挂长空，星稀云散夜清风。\n心中所念终有应，静待时来运自通。", "心想事成，宜耐心等待。时机将至，静则为吉。", FortuneLevel.GOOD},
            {6, "春风化雨", "春风化雨润无声，万物生辉气象新。\n贵人扶持功成就，恩泽广被福盈门。", "有贵人相助，事业有成。宜感恩图报，广施善缘。", FortuneLevel.GOOD},
            {7, "渔翁得利", "鹬蚌相争渔翁利，坐收其成不费力。\n莫与人争闲是非，退一步海阔天际。", "勿介入纷争，坐享其成。宜保持中立，以退为进。", FortuneLevel.GOOD},
            {8, "枯木逢春", "枯木逢春犹再发，生机盎然气象佳。\n柳暗花明又一村，绝处逢生福自华。", "困境将解，转机已现。宜重整旗鼓，再启新程。", FortuneLevel.GOOD},
            {9, "一帆风顺", "顺风扬帆行万里，水阔天长任尔驰。\n所向披靡无阻碍，功成圆满自有时。", "一路顺遂，所求如意。宜乘势前进，把握当下。", FortuneLevel.GOOD},
            // 中签 ×12
            {10, "守拙安分", "守拙安分度日长，不争不抢自安康。\n平淡之中藏真味，知足常乐福绵长。", "平平淡淡才是真。宜安守本分，勿生贪念。", FortuneLevel.NEUTRAL},
            {11, "塞翁失马", "塞翁失马焉知祸，祸福相倚古来多。\n得失之间须细辨，静观其变莫蹉跎。", "得失难料，宜静观其变。祸福相依，勿喜勿忧。", FortuneLevel.NEUTRAL},
            {12, "中流砥柱", "中流砥柱立波心，任凭风浪不沉沦。\n坚守本心终有报，功成不惧岁寒侵。", "宜坚守立场，不随波逐流。持之以恒，终有所成。", FortuneLevel.NEUTRAL},
            {13, "守得云开", "守得云开见月明，坚持到底事方成。\n莫因一时困且顿，轻言放弃负前程。", "宜坚持到底，勿半途而废。守得云开，自见月明。", FortuneLevel.NEUTRAL},
            {14, "随缘自在", "随缘自在度春秋，不强求兮不妄忧。\n命里有时终须有，命里无时莫强求。", "宜顺其自然，不强求。随缘而安，自在逍遥。", FortuneLevel.NEUTRAL},
            {15, "厚积薄发", "厚积薄发待其时，深藏不露养根基。\n一朝功成惊四座，皆因平日苦修持。", "宜厚积薄发，蓄势待发。根基既固，自有成时。", FortuneLevel.NEUTRAL},
            {16, "稳扎稳打", "稳扎稳打步步营，不急不躁事方成。\n欲速则不达古训，踏实前行福自迎。", "宜稳扎稳打，勿急功近利。脚踏实地，前途可期。", FortuneLevel.NEUTRAL},
            {17, "以和为贵", "以和为贵万事兴，和气致祥福满门。\n争斗伤身又伤情，和睦相处自长春。", "宜以和为贵，息事宁人。和睦相处，万事兴旺。", FortuneLevel.NEUTRAL},
            {18, "顺水推舟", "顺水推舟不费力，借势而为事易成。\n识时务者为俊杰，顺应潮流福自生。", "宜顺势而为，借力使力。顺应时势，事半功倍。", FortuneLevel.NEUTRAL},
            {19, "中庸之道", "中庸之道不偏倚，过犹不及皆非宜。\n恰到好处方为美，守中致和福自归。", "宜守中庸，勿走极端。恰到好处，方为上策。", FortuneLevel.NEUTRAL},
            {20, "韬光养晦", "韬光养晦待时机，锋芒不露自藏辉。\n大智若愚真境界，一鸣惊人天下知。", "宜韬光养晦，藏锋敛锐。时机一到，自可腾飞。", FortuneLevel.NEUTRAL},
            {21, "知足常乐", "知足常乐心自安，贪得无厌祸相连。\n粗茶淡饭亦清欢，心安理得福绵绵。", "宜知足常乐，勿贪勿求。心安理得，福报自至。", FortuneLevel.NEUTRAL},
            // 下签 ×6
            {22, "乌云蔽日", "乌云蔽日暗无光，前路茫茫多阻挡。\n莫急莫躁宜等待，云开雾散见太阳。", "暂时受阻，宜耐心等待。勿急躁冒进，静待转机。", FortuneLevel.BAD},
            {23, "逆水行舟", "逆水行舟不进退，进退维谷事难成。\n宜守不宜轻举妄，待时而动方为能。", "行事艰难，宜守不宜进。暂且按兵不动，等待时机。", FortuneLevel.BAD},
            {24, "口舌是非", "口舌是非惹人烦，谨言慎行保平安。\n莫听闲言莫轻传，少说多做福自还。", "宜谨言慎行，勿招是非。少言寡语，可保平安。", FortuneLevel.BAD},
            {25, "破财消灾", "破财消灾古来训，财散人安乐天伦。\n勿因小失大怨恨，失之东隅收桑榆。", "恐有破财，宜破财消灾。勿过分计较，失中有得。", FortuneLevel.BAD},
            {26, "孤掌难鸣", "孤掌难鸣势单薄，独力难支事难成。\n宜寻助力共谋事，众志成城业可兴。", "独力难成，宜寻合作。借助他人之力，方能有成。", FortuneLevel.BAD},
            {27, "前路崎岖", "前路崎岖多坎坷，步步维艰费周折。\n莫畏艰难勇向前，披荆斩棘道自阔。", "前路艰难，宜有心理准备。不畏险阻，终能通达。", FortuneLevel.BAD},
            // 下下签 ×3
            {28, "雪上加霜", "雪上加霜寒彻骨，祸不单行苦难度。\n宜守不宜轻举妄，静待春来冰雪融。", "运势低迷，宜静守勿动。韬光养晦，静待时来。", FortuneLevel.WORST},
            {29, "四面楚歌", "四面楚歌困重围，进退两难事可悲。\n宜忍宜让宜退避，留得青山再作为。", "处境艰难，宜忍让退避。留得青山在，不怕没柴烧。", FortuneLevel.WORST},
            {30, "镜花水月", "镜花水月空欢喜，虚幻一场终成空。\n莫贪莫恋莫执着，脚踏实地方为功。", "所求难成，恐是虚幻。宜认清现实，勿沉迷妄想。", FortuneLevel.WORST}
    };

    @Bean
    ApplicationRunner initFortunes(FortuneRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                log.info("签文库已存在，跳过初始化（共 {} 支签）", repository.count());
                return;
            }
            List<Fortune> fortunes = new java.util.ArrayList<>();
            for (Object[] row : FORTUNES) {
                fortunes.add(new Fortune(
                        (Integer) row[0],
                        (String) row[1],
                        (String) row[2],
                        (String) row[3],
                        (FortuneLevel) row[4]
                ));
            }
            repository.saveAll(fortunes);
            log.info("签文库初始化完成，共载入 {} 支签", fortunes.size());
        };
    }
}
