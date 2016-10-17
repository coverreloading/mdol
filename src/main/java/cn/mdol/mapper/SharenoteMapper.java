package cn.mdol.mapper;

import cn.mdol.po.Sharenote;
import cn.mdol.po.SharenoteExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SharenoteMapper {
    int countByExample(SharenoteExample example);

    int deleteByExample(SharenoteExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Sharenote record);

    int insertSelective(Sharenote record);

    List<Sharenote> selectByExample(SharenoteExample example);

    Sharenote selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Sharenote record, @Param("example") SharenoteExample example);

    int updateByExample(@Param("record") Sharenote record, @Param("example") SharenoteExample example);

    int updateByPrimaryKeySelective(Sharenote record);

    int updateByPrimaryKey(Sharenote record);
}