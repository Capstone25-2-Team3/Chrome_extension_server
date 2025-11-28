from openai import OpenAI, RateLimitError, APIError
import time

client = OpenAI()  # 환경변수 OPENAI_API_KEY 사용

def refine_text(text: str, labels: list[str]) -> dict:
    """
    원문과 혐오 라벨을 바탕으로 순화된 문장을 반환하는 함수
    반환 형식: { "original": 원문, "refined": 순화문장 }
    """

    # 프롬프트 구성
    system_prompt = (
        "당신은 온라인 상의 공격적인 표현을 부드럽게 바꾸는 전문가입니다.\n"
        "사용자의 문장에서 혐오 표현으로 의심되는 부분을 순화하여, 의미는 유지하되 감정적으로 자극적이지 않게 고쳐주세요.\n"
        "최종 응답은 반드시 **순화된 문장만** 출력해주세요. 다른 설명이나 문장 없이, 순화된 문장 하나만 주셔야 합니다."
    )

    user_prompt = (
        f"다음은 혐오 표현을 포함한 문장입니다:\n\n"
        f"[원문]: {text}\n"
        f"[혐오 라벨]: {', '.join(labels)}\n\n"
        f"이 문장을 의미를 유지하면서 부드럽게 순화해 주세요."
    )

    try:
        response = client.chat.completions.create(
            model="gpt-5.1",
            messages=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": user_prompt}
            ],
            temperature=0.7,
        )

        refined = response.choices[0].message.content.strip()

        return {
            "original": text,
            "refined": refined
        }

    except RateLimitError:
        print("❗️Rate Limit 초과: 잠시 대기 후 재시도합니다.")
        time.sleep(20)
        return {"original": text, "refined": "[Rate_Limit_Error]"}
    except APIError as e:
        print(f"❗️API 오류 발생: {e}")
        return {"original": text, "refined": "[API_Error]"}
    except Exception as e:
        print(f"❗️예기치 않은 오류 발생: {e}")
        return {"original": text, "refined": "[Unknown_Error]"}