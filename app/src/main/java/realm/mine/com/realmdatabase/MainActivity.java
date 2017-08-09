package realm.mine.com.realmdatabase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    //Lên search realm để add thư viện vào
    // bước làm nó có chỉ, nếu không nhớ ở đâu thì bước 1 là vào android đổi thành project
    // sau đó vào build.gradle sau đó vào lại android cũng vào build.gradle
    private ListView lvName;
    private ArrayAdapter<Task> adapter;
    private Realm realmDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvName = (ListView) findViewById(R.id.lvName);
        registerForContextMenu(lvName);
        //Khỏi tạo để tạo database realm (kết nối realm)
        Realm.init(this);
        //Chúng ta dùng nó để tạo bảng, truy vấn dữ liệu
        //Thêm, xóa, sửa
        realmDB = Realm.getDefaultInstance();
        //Đổ data lên Listview
        adapter = new ArrayAdapter<Task>(this,android.R.layout.simple_list_item_1,getTask());
        lvName.setAdapter(adapter);
        getSupportActionBar().setTitle("Quản lý công việc");
    }
    private void reloadDB(){
        adapter = new ArrayAdapter<Task>(this,android.R.layout.simple_list_item_1,getTask());
        lvName.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    private RealmResults<Task> getTask(){
        RealmResults<Task> listData = realmDB.where(Task.class).findAll();

        return listData;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.mnAdd){
            //Thêm dữ liệu vào database realm
            realmDB.executeTransaction(new Realm.Transaction(){

                @Override
                public void execute(Realm realm) {
                    Random rd = new Random();
                    Task myTask = new Task(rd.nextInt(10),"Code tính năng" + rd.nextInt(100));
                    realm.copyToRealmOrUpdate(myTask);
                    reloadDB();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Task selectedItem = adapter.getItem(info.position);
        //Truy vấn để lấy đối tượng từ realm
        final Task myTask = realmDB.where(Task.class).equalTo("id",selectedItem.getId()).findFirst();
        if(myTask != null){
            switch (item.getItemId()){
                case R.id.mnDelete:
                    //Transaction thủ công
                    realmDB.beginTransaction();
                    myTask.deleteFromRealm();
                    realmDB.commitTransaction();
                    break;
                case R.id.mnUpdate:
                    //Nên dùng transaction như này
                    realmDB.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Random rd = new Random();
                            //Tự update rồi
                            myTask.setName("Phát hành app: " + rd.nextInt(20));
                        }
                    });
                    break;
            }
            Toast.makeText(this, "Cập nhật dữ liệu thành công", Toast.LENGTH_SHORT).show();
            reloadDB();
        }else{
            Toast.makeText(this, "Thao tác không thành công", Toast.LENGTH_SHORT).show();
        }
        return super.onContextItemSelected(item);
    }
}
