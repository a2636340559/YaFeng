package com.poet.yafeng.DAO;

import com.poet.yafeng.POJO.Poetry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PoetryDAO extends JpaRepository<Poetry,Integer> {
    /*
  @获取一首古诗
   */
    @Query(value = "select * from poetry order by rand( ) limit 1",nativeQuery = true)
    public Poetry getOnePoetry();

    /*
    @根据诗名及作者获取古诗详情
     */
    @Query(value = "select * from poetryDetail where name =?1 and author=?2 ",nativeQuery = true)
    public Poetry getPoetryDetail(String name,String author);

    /*
    @根据诗名和作者获取古诗
     */
    @Query(value = "select * from poetry where name=?1 and author=?2",nativeQuery = true)
    public List<Poetry> getPoetryBy(String name,String author);

    /*
    @获取所有朝代
     */
    @Query(value ="select dynasty from poetry group by dynasty",nativeQuery = true)
    public List<String> getAllDynasty();

    /*
    @获取所有诗词类型
     */
    @Query(value = "select tag from Tag where flag='类型'",nativeQuery = true)
    public List<String> getAllPoemStyle();

    /*
    @根据诗词内容获取诗词详情
     */
    @Query(value = "select * from poetryDetail where content like %?1"+"%",nativeQuery = true)
    public Poetry getPoetryDetailBy(String content);

    /*
    @根据搜索框内容获取诗词
     */
    @Query(value = "select * from poetry where name=?1 or author=?2 or content like %?3"+"%",nativeQuery = true)
    public Page<Poetry> getPoetryBySearch(String name,String author,String content,Pageable pageable);

    /*
    @根据标签分类获取诗词
     */
    @Query(value = "select * from poetry where tag like  ?1 or dynasty=?2 ",nativeQuery = true)
    public Page<Poetry> getPoetryByTag(String tag,String dynasty,Pageable pageable);

    /*
    @判断该作者的诗是否存在
     */
    @Query(value = "select 1 from poetry where author = ?1 limit 1 ",nativeQuery = true)
    public Integer isExistAuthor(String author);

    /*
    @判断该诗名的诗是否存在
     */
    @Query(value = "select 1 from poetry where name = ?1 limit 1 ",nativeQuery = true)
    public Integer isExistTitle(String title);


    @Query(value = "select * from poetry where author='杜甫'",nativeQuery = true)
    public Page<Poetry> get(Pageable pageable);
}
