package contracts.gettweets

import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    request {
        method 'POST'
        url '/v1/tweets'
        headers {
            header('Authorization', 'Basic dXNlcjpwYXNzd29yZA==')
        }
        body (["text":"tweet1"])
    }
    response {
        status 200
        body([
                [
                        tweetId  : '00000000-0000-0000-0000-000000000000',
                        text     : 'tweet1',
                        username : 'user',
                        createdAt: $(client('2017-04-12T01:09:05Z'), server(regex('[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}Z')))
                ]
        ])
        headers {
            contentType('application/json')
        }
    }
}