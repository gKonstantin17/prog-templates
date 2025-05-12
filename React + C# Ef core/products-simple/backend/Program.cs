using kis;
using System.Collections.Generic;
// � specification ������� truncate restart indentity
// 
/*
INSERT INTO public."Specification"("Level", "Name", "Count", "Parent_id") VALUES
-- �������� ������� - ���������� (������� 0)
(0, '����������', 1, NULL),

-- �������� ���������� (������� 1)
(1, '����� �������', 2, 1),
(1, '����� ������������', 2, 1),
(1, '������', 1, 1),
(1, '���������', 1, 1),

-- ���������� ��������� (������� 2)
(2, '������ ��� �������', 1, 5),
(2, '������������ �������', 1, 5),

-- �������������� ��������� (������� 1)
(1, '�����', 1, 1),
(1, '����������', 1, 1),
(1, '����', 1, 1),

-- ���������� ������ (������� 1)
(1, '������������ ��������', 1, 1),
(2, '�����', 10, 10),
(2, '������', 5, 10),
(2, '����������', 1, 10);
*/

public class Program
{
    public static void Main(string[] args)
    {
        CreateHostBuilder(args).Build().Run();
    }
    public static IHostBuilder CreateHostBuilder(string[] args)
    => Host.CreateDefaultBuilder(args)
        .ConfigureWebHostDefaults(
            webBuilder => webBuilder.UseStartup<Startup>()
            .UseUrls("http://0.0.0.0:5144") // ������� �� ���� IP
            );
}