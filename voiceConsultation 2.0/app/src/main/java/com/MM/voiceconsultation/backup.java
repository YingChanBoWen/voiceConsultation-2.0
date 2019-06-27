package com.MM.voiceconsultation;

public class backup {
    /*if(question != null){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url = "http://192.168.1.114:60000";
                        Map<String,String > parms = new HashMap<String, String>();
                        parms.put("question",text);
                        String res = HTTP.getContextByHttp(url+"/question", parms,"POST");


                        Log.d("result",res);



                        answer.append(res);


                        final Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                tvAnswer.setText(answer.toString());
                            }
                        };
                        handler.post(runnable);


                    }
                }).start();
            }*/
            /*String regex1 = ".*肚子疼.*";
            String regex2 = ".*吐.*";
            String regex3 = ".*没.*";
            String temp = text;
            if (isLast == false){
                //Log.d("islast", question.toString());
                //Log.d("bool",Pattern.matches(regex1, temp)+"");
                if (Pattern.matches(regex1, temp)) {
                    answer.append("还有其他症状吗?\n");
                    tvAnswer.setText(answer.toString());
                } else if (Pattern.matches(regex2, temp)) {
                    answer.append("还有吗?\n");
                    tvAnswer.setText(answer.toString());
                } else if (Pattern.matches(regex3, temp)) {
                    answer.append("阑尾炎!\n");
                    tvAnswer.setText(answer.toString());
                }
                else{
                    answer.append("？!\n");
                    tvAnswer.setText(answer.toString());
                }

            }*/

            /*//
            final String temp = text;
            if(isLast == false){
                try{
                    for (int i = 0; i < json.length(); i++) {

                        String name = json.getJSONObject(i).getString("chinese_name");
                        String regex = ".*" + name + ".*";
                        if (Pattern.matches(regex, temp)) {
                            Log.d("pattern", name);
                            answer.append(json.getJSONObject(i) + "\n");


                            final Runnable runnable = new Runnable() {
                                @Override
                                public void run() {

                                    tvAnswer.setText(answer.toString());
                                }
                            };
                            handler.post(runnable);
                        }

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }


            }
            //*/
}
