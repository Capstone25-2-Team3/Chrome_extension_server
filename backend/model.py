def predict_profanity(sentence: str):
    '''
    실제 KoBERT 모델이 들어갈 자리
    지금은 더미 값 반환 
    '''
    return {
        "isProfanity" : 1 if "욕" in sentence else 0,
        "highlighted" : ["욕"] if "욕" in sentence else [],
        "confidence" : 0.87 # 예시 
    }