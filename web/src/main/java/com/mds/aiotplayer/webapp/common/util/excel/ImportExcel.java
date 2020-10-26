/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.common.util.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.utils.Reflections;
import com.mds.aiotplayer.util.excel.ExcelImportResult;
import com.mds.aiotplayer.common.utils.excel.annotation.ExcelField;
import com.mds.aiotplayer.util.DictUtils;

/**
 * 导入Excel文件（支持“XLS”和“XLSX”格式）
 * @author ThinkGem
 * @version 2013-03-10
 */
public class ImportExcel extends BasicExcel {
	
	private static Logger log = LoggerFactory.getLogger(ImportExcel.class);
			
	/**
	 * 工作薄对象
	 */
	private Workbook wb;
	
	/**
	 * 工作表对象
	 */
	private Sheet sheet;
	
	/**
	 * 标题行号
	 */
	private int headerNum;
	
	/**
	 * 构造函数
	 * @param path 导入文件，读取第一个工作表
	 * @param headerNum 标题行号，数据行号=标题行号+1
	 * @throws InvalidFormatException 
	 * @throws IOException 
	 */
	public ImportExcel(String fileName, int headerNum) 
			throws InvalidFormatException, IOException {
		this(new File(fileName), headerNum);
	}
	
	/**
	 * 构造函数
	 * @param path 导入文件对象，读取第一个工作表
	 * @param headerNum 标题行号，数据行号=标题行号+1
	 * @throws InvalidFormatException 
	 * @throws IOException 
	 */
	public ImportExcel(File file, int headerNum) 
			throws InvalidFormatException, IOException {
		this(file, headerNum, 0);
	}

	/**
	 * 构造函数
	 * @param path 导入文件
	 * @param headerNum 标题行号，数据行号=标题行号+1
	 * @param sheetIndex 工作表编号
	 * @throws InvalidFormatException 
	 * @throws IOException 
	 */
	public ImportExcel(String fileName, int headerNum, int sheetIndex) 
			throws InvalidFormatException, IOException {
		this(new File(fileName), headerNum, sheetIndex);
	}
	
	/**
	 * 构造函数
	 * @param path 导入文件对象
	 * @param headerNum 标题行号，数据行号=标题行号+1
	 * @param sheetIndex 工作表编号
	 * @throws InvalidFormatException 
	 * @throws IOException 
	 */
	public ImportExcel(File file, int headerNum, int sheetIndex) 
			throws InvalidFormatException, IOException {
		this(file.getName(), new FileInputStream(file), headerNum, sheetIndex);
	}
	
	/**
	 * 构造函数
	 * @param file 导入文件对象
	 * @param headerNum 标题行号，数据行号=标题行号+1
	 * @param sheetIndex 工作表编号
	 * @throws InvalidFormatException 
	 * @throws IOException 
	 */
	public ImportExcel(MultipartFile multipartFile, int headerNum, int sheetIndex) 
			throws InvalidFormatException, IOException {
		this(multipartFile.getOriginalFilename(), multipartFile.getInputStream(), headerNum, sheetIndex);
	}

	/**
	 * 构造函数
	 * @param path 导入文件对象
	 * @param headerNum 标题行号，数据行号=标题行号+1
	 * @param sheetIndex 工作表编号
	 * @throws InvalidFormatException 
	 * @throws IOException 
	 */
	public ImportExcel(String fileName, InputStream is, int headerNum, int sheetIndex) 
			throws InvalidFormatException, IOException {
		if (StringUtils.isBlank(fileName)){
			throw new RuntimeException("导入文档为空!");
		}else if(fileName.toLowerCase().endsWith("xls")){    
			this.wb = new HSSFWorkbook(is);    
        }else if(fileName.toLowerCase().endsWith("xlsx")){  
        	this.wb = new XSSFWorkbook(is);
        }else{  
        	throw new RuntimeException("invalid file format!");
        }  
		if (this.wb.getNumberOfSheets()<sheetIndex){
			throw new RuntimeException("文档中没有工作表!");
		}
		this.sheet = this.wb.getSheetAt(sheetIndex);
		this.headerNum = headerNum;
		log.debug("Initialize success.");
	}
	
	/**
	 * 获取行对象
	 * @param rownum
	 * @return
	 */
	public Row getRow(int rownum){
		return this.sheet.getRow(rownum);
	}

	/**
	 * 获取数据行号
	 * @return
	 */
	public int getDataRowNum(){
		return headerNum+1;
	}
	
	/**
	 * 获取最后一个数据行号
	 * @return
	 */
	public int getLastDataRowNum(){
		return this.sheet.getLastRowNum()+headerNum;
	}
	
	/**
	 * 获取最后一个列号
	 * @return
	 */
	public int getLastCellNum(){
		return this.getRow(headerNum).getLastCellNum();
	}
	
	/**
	 * 获取单元格值
	 * @param row 获取的行
	 * @param column 获取单元格列号
	 * @return 单元格值
	 */
	public Object getCellValue(Row row, int column){
		Object val = "";
		try{
			Cell cell = row.getCell(column);
			if (cell != null){
				if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
					val = cell.getNumericCellValue();
				}else if (cell.getCellType() == Cell.CELL_TYPE_STRING){
					val = cell.getStringCellValue();
				}else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA){
					val = cell.getCellFormula();
				}else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN){
					val = cell.getBooleanCellValue();
				}else if (cell.getCellType() == Cell.CELL_TYPE_ERROR){
					val = cell.getErrorCellValue();
				}
			}
		}catch (Exception e) {
			return val;
		}
		return val;
	}
	
	/**
	 * 获取导入数据列表
	 * @param cls 导入对象类型
	 * @param groups 导入分组
	 */
	@SuppressWarnings("unchecked")
	public <E> ExcelImportResult<E> getDataList(Class<E> cls, int... groups) throws InstantiationException, IllegalAccessException{
		List<Object[]> annotationList = getExcelField(null, 0, cls, 2, groups);
		// Field sorting
		Collections.sort(annotationList, new Comparator<Object[]>() {
			public int compare(Object[] o1, Object[] o2) {
				return new Integer(getSort(o1)).compareTo(
						new Integer(getSort(o2)));
			};
		});
		
		//log.debug("Import column count:"+annotationList.size());
		// Get excel data
		ExcelImportResult<E> dataList = ExcelImportResult.newInstance();//Lists.newArrayList();
		for (int i = this.getDataRowNum(); i < this.getLastDataRowNum(); i++) {
			E e = (E)cls.newInstance();
			int column = 0;
			Row row = this.getRow(i);
			StringBuilder sb = new StringBuilder();
			for (Object[] os : annotationList){
				Object val = this.getCellValue(row, column++);
				if (val != null){
					ExcelField ef = (ExcelField)os[0];
					// If is dict type, get dict value
					if (StringUtils.isNotBlank(ef.dictType())){
						val = DictUtils.getDictValue(val.toString(), ef.dictType(), "");
						//log.debug("Dictionary type value: ["+i+","+colunm+"] " + val);
					}
					// Get param type and type cast
					Class<?> valType = getValueType(e, os, val);
					//log.debug("Import value type: ["+i+","+column+"] " + valType);
					
					try {
						if (valType == String.class){
							String s = String.valueOf(val.toString());
							if(StringUtils.endsWith(s, ".0")){
								val = StringUtils.substringBefore(s, ".0");
							}else{
								val = String.valueOf(val.toString());
							}
						}else if (valType == Integer.class){
							val = Double.valueOf(val.toString()).intValue();
						}else if (valType == Long.class){
							val = Double.valueOf(val.toString()).longValue();
						}else if (valType == Double.class){
							val = Double.valueOf(val.toString());
						}else if (valType == Float.class){
							val = Float.valueOf(val.toString());
						}else if (valType == Boolean.class || valType == boolean.class){
							val = (val.toString().equalsIgnoreCase("y")||val.toString().equals("1")) ? true : false;	
						}else if (valType == Date.class){
							val = DateUtil.getJavaDate((Double)val);
						}else if (valType.isEnum()){
							//val = Enum.valueOf((Class<T>) valType, val.toString());
							if (!StringUtils.isBlank(val.toString())) {
								val = valType.getMethod("valueOf", String.class).invoke(null, val.toString());
							}else {
								val = null;
							}
						}else{
							if (ef.complex() == 1){
								/*val = Class.forName(ExcelField.class.getName().replaceAll("annotation.ExcelField", 
										"fieldcell."+valType.getSimpleName()+"Type")).getMethod("getParentValue", List.class, String.class).invoke(null, dataList.toList(), val.toString());*/
								/*val = Class.forName(getFieldTypePackage() + ".fieldcell."+valType.getSimpleName()+"Cell")
										.getMethod("getParentValue", List.class, String.class).invoke(null, dataList.toList(), val.toString());*/
								e = (E)Class.forName(getFieldTypePackage() + ".fieldcell."+valType.getSimpleName()+"Cell")
										.getMethod("getFullCode", Object.class, String.class).invoke(null, e, val.toString());
								val=null;
							}else if (ef.complex() == 2){
								e = (E)Class.forName(getFieldTypePackage() + ".fieldcell."+valType.getSimpleName()+"Cell")
										.getMethod("getSemiDelimitedValue", cls, String.class).invoke(null, e, val.toString());
							}else{
								if (ef.fieldType() != Class.class){
									val = ef.fieldType().getMethod("getValue", String.class).invoke(null, val.toString());
								}else{
									/*val = Class.forName(this.getClass().getName().replaceAll(this.getClass().getSimpleName(), 
											"fieldcell."+valType.getSimpleName()+"Type")).getMethod("getValue", String.class).invoke(null, val.toString());*/
									/*val = Class.forName(ExcelField.class.getName().replaceAll("annotation.ExcelField", 
											"fieldcell."+valType.getSimpleName()+"Type")).getMethod("getValue", String.class).invoke(null, val.toString());*/
									val = Class.forName(getFieldTypePackage() + ".fieldcell."+valType.getSimpleName()+"Cell")
											.getMethod("getValue", String.class).invoke(null, val.toString());
								}
							}
						}
					} catch (Exception ex) {
						log.info("Get cell value ["+i+","+column+"] error: " + ex.toString());
						dataList.addResult(i, "Get cell value ["+i+","+column+"] error: " + ex.toString());
						val = null;
					}
					// set entity value
					/*if (os[1] instanceof Field){
						Reflections.invokeSetter(e, ((Field)os[1]).getName(), val);
					}else if (os[1] instanceof Method){
						String mthodName = ((Method)os[1]).getName();
						if ("get".equals(mthodName.substring(0, 3))){
							mthodName = "set"+StringUtils.substringAfter(mthodName, "get");
						}
						Reflections.invokeMethod(e, mthodName, new Class[] {valType}, new Object[] {val});
					}*/
					setValue(e, os, valType, val);
				}
				sb.append(val+", ");
			}
			if (!dataList.hasErrors(i))
				dataList.add(i, e);
			else
				log.debug("Read success: ["+i+"] "+sb.toString());
		}
		return dataList;
	}
	
	// set entity value
	private <E> Class<?> getValueType(E efirst, Object[] os, Object val){
		Object e = efirst;
		int emLevel = 0;
		ExcelField ef = null;
		Class<?> valType = Class.class;
		while(os.length > emLevel){
			ef = (ExcelField)os[emLevel];
			emLevel += 2;
			if (os.length > emLevel){
				Object objVal = null;
				try{
					if (os[emLevel-1] instanceof Field){
						objVal = Reflections.invokeGetter(e, ((Field)os[emLevel-1]).getName());
					}else if (os[emLevel-1] instanceof Method){
						objVal = Reflections.invokeMethod(e, ((Method)os[emLevel-1]).getName(), new Class[] {}, new Object[] {});
					}
				}catch(Exception ex) {
					// Failure to ignore
					log.info(ex.toString());
					objVal = e;
				}
				e = objVal;
			}else{
				// Get param type and type cast
				if (os[emLevel-1] instanceof Field){
					valType = ((Field)os[emLevel-1]).getType();
				}else if (os[emLevel-1] instanceof Method){
					Method method = ((Method)os[emLevel-1]);
					if ("get".equals(method.getName().substring(0, 3))){
						valType = method.getReturnType();
					}else if("set".equals(method.getName().substring(0, 3))){
						valType = ((Method)os[emLevel-1]).getParameterTypes()[0];
					}
				}
			}
		}
		
		return valType;
	}
	
	// set entity value
	private <E> void setValue(E efirst, Object[] os, Class<?> valType, Object val){
		Object e = efirst;
		int emLevel = 0;
		ExcelField ef = null;
		while(os.length > emLevel){
			ef = (ExcelField)os[emLevel];
			emLevel += 2;
			if (os.length > emLevel){
				Object objVal = null;
				try{
					if (os[emLevel-1] instanceof Field){
						objVal = Reflections.invokeGetter(e, ((Field)os[emLevel-1]).getName());
					}else if (os[emLevel-1] instanceof Method){
						objVal = Reflections.invokeMethod(e, ((Method)os[emLevel-1]).getName(), new Class[] {}, new Object[] {});
					}
				}catch(Exception ex) {
					// Failure to ignore
					log.info(ex.toString());
					objVal = e;
				}
				e = objVal;
			}else{
				if (os[emLevel-1] instanceof Field){
					Reflections.invokeSetter(e, ((Field)os[emLevel-1]).getName(), val);
				}else if (os[emLevel-1] instanceof Method){
					String mthodName = ((Method)os[emLevel-1]).getName();
					if ("get".equals(mthodName.substring(0, 3))){
						mthodName = "set"+StringUtils.substringAfter(mthodName, "get");
					}
					Reflections.invokeMethod(e, mthodName, new Class[] {valType}, new Object[] {val});
				}
			}
		}
	}

//	/**
//	 * Import test
//	 */
//	public static void main(String[] args) throws Throwable {
//		
//		ImportExcel ei = new ImportExcel("target/export.xlsx", 1);
//		
//		for (int i = ei.getDataRowNum(); i < ei.getLastDataRowNum(); i++) {
//			Row row = ei.getRow(i);
//			for (int j = 0; j < ei.getLastCellNum(); j++) {
//				Object val = ei.getCellValue(row, j);
//				System.out.print(val+", ");
//			}
//			System.out.print("\n");
//		}
//		
//	}

}
