import http.client
import json

payload = ""

conn = http.client.HTTPConnection("solr1:8983")

conn.request("GET", "/solr/admin/collections?action=LIST&wt=json", payload)

res = conn.getresponse()
data = res.read()
parsed_json = (json.loads(data))

# Check if collection is already present
if 'mentions' not in parsed_json['collections']:
    # Create collection if not present
    conn.request("GET", "/solr/admin/collections?action=CREATE&name=mentions&numShards=1&replicationFactor=1", payload)

    res = conn.getresponse()
    data = res.read()

    print(json.dumps(json.loads(data), indent=4, sort_keys=True))
else:
    print("Collection mentions already present")

if 'resources' not in parsed_json['collections']:
    # Create collection if not present
    conn.request("GET", "/solr/admin/collections?action=CREATE&name=resources&numShards=1&replicationFactor=1", payload)
    res = conn.getresponse()
    data = res.read()

    #Configure schema
    payload = "{\n  \"add-field\":{\n      \"name\": \"date\",\n      \"type\": \"pdates\"\n    }\n}"
    conn.request("POST", "/solr/resources/schema", payload)
    conn.getresponse()

    payload = "{\n  \"add-field\":{\n      \"name\": \"url\",\n      \"type\": \"text_general\"\n    }\n}"
    conn.request("POST", "/solr/resources/schema", payload)
    conn.getresponse()


    print(json.dumps(json.loads(data), indent=4, sort_keys=True))
else:
    print("Collection resources already present")