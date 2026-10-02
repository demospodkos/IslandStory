using UnityEngine;
using UnityEngine.UI;
using UnityEngine.Video;

public class IslandStoryGame : MonoBehaviour
{
    VideoPlayer video; RawImage screen; Text title, dialog, objective;
    Button left, right, jump, next, start;
    float px=.18f, py=.22f, vy; bool playing; int chapter;
    readonly string[] names={"ДОРОГА НА ОСТРОВ","ДЖУНГЛИ","ВСТРЕЧА","АЙБОЛИТ","ПОДКРЕПЛЕНИЕ","ФИНАЛ"};
    readonly string[] story={
      "Таня и Ваня снова слышат предупреждение: в Африку не ходить.",
      "Но любопытство побеждает. Впереди джунгли и неизвестность.",
      "Из кустов появляется огромный бегемот. Нужно выбраться к тропе.",
      "Айболит замечает детей и пытается разобраться, что произошло.",
      "На помощь прибывает полиция. Нужно добраться до лодки.",
      "Дети возвращаются домой. История заканчивается."
    };
    void Start(){Screen.orientation=ScreenOrientation.LandscapeLeft; BuildUI(); SetupVideo(); Menu();}
    Text Txt(Transform p,string n,int size){var g=new GameObject(n);g.transform.SetParent(p,false);var t=g.AddComponent<Text>();t.font=Resources.GetBuiltinResource<Font>("Arial.ttf");t.fontSize=size;t.color=Color.white;return t;}
    Button Btn(Transform p,string s,Vector2 a){var g=new GameObject(s);g.transform.SetParent(p,false);var b=g.AddComponent<Button>();var im=g.AddComponent<Image>();im.color=new Color(0,0,0,.72f);b.targetGraphic=im;var r=g.GetComponent<RectTransform>();r.anchorMin=a;r.anchorMax=a;r.sizeDelta=new Vector2(125,70);var t=Txt(g.transform,"label",28);t.text=s;t.alignment=TextAnchor.MiddleCenter;t.rectTransform.anchorMin=Vector2.zero;t.rectTransform.anchorMax=Vector2.one;return b;}
    void BuildUI(){
      var c=new GameObject("Canvas").AddComponent<Canvas>();c.renderMode=RenderMode.ScreenSpaceOverlay;
      var sc=c.gameObject.AddComponent<CanvasScaler>();sc.uiScaleMode=CanvasScaler.ScaleMode.ScaleWithScreenSize;sc.referenceResolution=new Vector2(1280,720);c.gameObject.AddComponent<GraphicRaycaster>();
      var vg=new GameObject("Video").AddComponent<RawImage>();vg.transform.SetParent(c.transform,false);screen=vg;screen.rectTransform.anchorMin=Vector2.zero;screen.rectTransform.anchorMax=Vector2.one;screen.rectTransform.offsetMin=Vector2.zero;screen.rectTransform.offsetMax=Vector2.zero;
      title=Txt(c.transform,"Title",44);title.alignment=TextAnchor.UpperCenter;title.rectTransform.anchorMin=new Vector2(.1f,.88f);title.rectTransform.anchorMax=new Vector2(.9f,.99f);
      dialog=Txt(c.transform,"Dialog",28);dialog.alignment=TextAnchor.MiddleCenter;dialog.rectTransform.anchorMin=new Vector2(.08f,.02f);dialog.rectTransform.anchorMax=new Vector2(.92f,.18f);
      objective=Txt(c.transform,"Objective",24);objective.rectTransform.anchorMin=new Vector2(.02f,.8f);objective.rectTransform.anchorMax=new Vector2(.5f,.88f);
      left=Btn(c.transform,"◀",new Vector2(.05f,.09f));right=Btn(c.transform,"▶",new Vector2(.16f,.09f));jump=Btn(c.transform,"▲",new Vector2(.87f,.09f));next=Btn(c.transform,"ДАЛЬШЕ",new Vector2(.77f,.88f));start=Btn(c.transform,"НАЧАТЬ",new Vector2(.5f,.5f));
      left.onClick.AddListener(()=>Move(-.03f));right.onClick.AddListener(()=>Move(.03f));jump.onClick.AddListener(Jump);next.onClick.AddListener(Next);start.onClick.AddListener(StartGame);
    }
    void SetupVideo(){var g=new GameObject("VideoPlayer");video=g.AddComponent<VideoPlayer>();video.playOnAwake=false;video.renderMode=VideoRenderMode.APIOnly;video.isLooping=false;video.prepareCompleted+=v=>screen.texture=v.texture;}
    void Menu(){playing=false;screen.color=new Color(.04f,.06f,.09f);title.text="ISLAND STORY";dialog.text="Сюжетная игра по твоему ролику";objective.text="Таня • Ваня • Айболит • бегемот • полиция";start.gameObject.SetActive(true);left.gameObject.SetActive(false);right.gameObject.SetActive(false);jump.gameObject.SetActive(false);next.gameObject.SetActive(false);}
    void StartGame(){start.gameObject.SetActive(false);left.gameObject.SetActive(true);right.gameObject.SetActive(true);jump.gameObject.SetActive(true);next.gameObject.SetActive(true);chapter=0;playing=true;PlayChapter();}
    void PlayChapter(){title.text=names[chapter];dialog.text=story[chapter];objective.text=chapter<5?"Цель: двигайся вперёд до конца главы":"Цель: нажми ДАЛЬШЕ";px=.18f;py=.22f;vy=0;if(video){video.Stop();video.url=System.IO.Path.Combine(Application.streamingAssetsPath,"story.mp4");video.time=chapter*24;video.Play();}}
    void Update(){if(!playing)return;vy-=.0008f;py+=vy;if(py<.22f){py=.22f;vy=0;}if(px>=.88f)Next();}
    void Move(float d){if(!playing)return;px=Mathf.Clamp01(px+d);objective.text="Цель: двигайся вперёд — "+Mathf.RoundToInt(px*100)+"%";}
    void Jump(){if(playing&&py<=.23f)vy=.018f;}
    void Next(){if(!playing)return;if(chapter<5){chapter++;PlayChapter();}else{video.Stop();playing=false;title.text="КОНЕЦ";dialog.text="Таня и Ваня вернулись домой. Спасибо за прохождение.";objective.text="Нажми НАЧАТЬ для повторной игры";next.gameObject.SetActive(false);start.gameObject.SetActive(true);}}
}